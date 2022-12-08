package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.DefnTypeClassifier
import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateChildContext}
import io.github.effiban.scala2java.core.matchers.TemplateChildContextMatcher.eqTemplateChildContext
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.orderings.JavaTemplateChildOrdering
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.TypeNames
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any

import scala.meta.{Ctor, Decl, Defn, Init, Lit, Name, Pat, Term, Tree, Type}

class TemplateBodyTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val ChildContextWithClassNameAndNoCtorTerms = TemplateChildContext(
    javaScope = JavaScope.Class,
    maybeClassName = Some(ClassName),
    inits = TheInits
  )

  private val DataMemberDecl = Decl.Val(
    mods = Nil,
    pats = List(Pat.Var(name = Term.Name("x"))),
    decltpe = TypeNames.Int
  )

  private val DataMemberDefn = Defn.Val(
    mods = Nil,
    pats = List(Pat.Var(name = Term.Name("y"))),
    decltpe = None,
    rhs = Lit.Int(4)
  )

  private val TypeDefn = Defn.Type(
    mods = Nil,
    name = Type.Name("X"),
    tparams = Nil,
    body = Type.Name("Y")
  )

  private val PrimaryCtorArgs = List(
    termParam("arg1", "Int"),
    termParam("arg2", "String")
  )
  private val SecondaryCtorArgs = List(
    termParam("arg3", "Int"),
    termParam("arg4", "String")
  )

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val SecondaryCtor = Ctor.Secondary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(SecondaryCtorArgs),
    init = Init(tpe = Type.Singleton(Term.This(Name.Anonymous())), name = Name.Anonymous(), argss = List(Nil)),
    stats = Nil
  )

  private val TermApply1 = Term.Apply(fun = Term.Name("doSomething1"), args = List(Term.Name("param1")))
  private val TermApply2 = Term.Apply(fun = Term.Name("doSomething2"), args = List(Term.Name("param2")))

  private val MethodDefn = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(List(termParam("param", "Int"))),
    decltpe = Some(TypeNames.Int),
    body = TermApply1
  )

  private val ChildOrder = List[Tree](
    DataMemberDecl,
    DataMemberDefn,
    PrimaryCtor,
    SecondaryCtor,
    MethodDefn,
    TypeDefn,
  )


  private val javaTemplateChildOrdering = mock[JavaTemplateChildOrdering]
  private val templateChildTraverser = mock[TemplateChildTraverser]
  private val defnTypeClassifier = mock[DefnTypeClassifier]

  private val templateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildTraverser,
    defnTypeClassifier,
    javaTemplateChildOrdering
  )

  test("traverse when empty") {

    templateBodyTraverser.traverse(stats = Nil, context = TemplateBodyContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has inits only") {
    templateBodyTraverser.traverse(stats = Nil, context = TemplateBodyContext(javaScope = JavaScope.Class, inits = TheInits))

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has primary ctor. only") {
    val context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )

    val expectedChildContext = TemplateChildContext(javaScope = JavaScope.Class, maybeClassName = Some(ClassName))
    expectWritePrimaryCtor(expectedChildContext)

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = Nil, context = context)

    outputWriter.toString shouldBe
      """ {
        |/*
        |*  PRIMARY CTOR
        |*/
        |}
        |""".stripMargin
  }


  test("traverse when has regular stats only") {
    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
    )

    expectWriteDataMemberDecl()
    expectWriteDataMemberDefn()
    expectWriteMethodDefn()

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, context = TemplateBodyContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has regular stats and non-enum type def") {
    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      TypeDefn
    )

    expectWriteDataMemberDecl()
    expectWriteDataMemberDefn()
    expectWriteMethodDefn()
    when(defnTypeClassifier.isEnumTypeDef(eqTree(TypeDefn), ArgumentMatchers.eq(JavaScope.Class))).thenReturn(false)
    expectWriteTypeDefn()

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, context = TemplateBodyContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  METHOD DEFINITION
        |*/
        |/*
        |*  TYPE DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has regular stats and enum type def should skip the type def") {
    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      TypeDefn
    )

    expectWriteDataMemberDecl()
    expectWriteDataMemberDefn()
    expectWriteMethodDefn()
    when(defnTypeClassifier.isEnumTypeDef(eqTree(TypeDefn), ArgumentMatchers.eq(JavaScope.Class))).thenReturn(true)

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, context = TemplateBodyContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has everything except loose terms") {
    val context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      inits = TheInits
    )

    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor
    )

    expectWriteDataMemberDecl(ChildContextWithClassNameAndNoCtorTerms)
    expectWriteDataMemberDefn(ChildContextWithClassNameAndNoCtorTerms)
    expectWritePrimaryCtor(ChildContextWithClassNameAndNoCtorTerms)
    expectWriteSecondaryCtor(ChildContextWithClassNameAndNoCtorTerms)
    expectWriteMethodDefn(ChildContextWithClassNameAndNoCtorTerms)

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, context = context)

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  PRIMARY CTOR
        |*/
        |/*
        |*  SECONDARY CTOR
        |*/
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has everything including loose terms") {
    val context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      inits = TheInits
    )

    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor,
      TermApply1,
      TermApply2
    )

    val expectedChildContext = TemplateChildContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      inits = TheInits,
      ctorTerms = List(TermApply1, TermApply2)
    )

    expectWriteDataMemberDecl(expectedChildContext)
    expectWriteDataMemberDefn(expectedChildContext)
    expectWritePrimaryCtor(expectedChildContext)
    expectWriteSecondaryCtor(expectedChildContext)
    expectWriteMethodDefn(expectedChildContext)

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, context = context)

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  PRIMARY CTOR
        |*/
        |/*
        |*  SECONDARY CTOR
        |*/
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def expectWriteDataMemberDecl(expectedChildContext: TemplateChildContext = TemplateChildContext(javaScope = JavaScope.Class)): Unit = {
    doWrite(
      """/* DATA MEMBER DECL */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDecl), eqTemplateChildContext(expectedChildContext))
  }

  private def expectWriteDataMemberDefn(expectedChildContext: TemplateChildContext = TemplateChildContext(javaScope = JavaScope.Class)): Unit = {
    doWrite(
    """/* DATA MEMBER DEFINITION */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDefn), eqTemplateChildContext(expectedChildContext))
  }

  private def expectWritePrimaryCtor(expectedChildContext: TemplateChildContext): Unit = {
    doWrite(
      """/*
        |*  PRIMARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(PrimaryCtor), eqTemplateChildContext(expectedChildContext))
  }

  private def expectWriteSecondaryCtor(expectedChildContext: TemplateChildContext): Unit = {
    doWrite(
      """/*
        |*  SECONDARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(SecondaryCtor), eqTemplateChildContext(expectedChildContext))
  }

  private def expectWriteMethodDefn(expectedChildContext: TemplateChildContext = TemplateChildContext(javaScope = JavaScope.Class)): Unit = {
    doWrite(
      """/*
        |*  METHOD DEFINITION
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(MethodDefn), eqTemplateChildContext(expectedChildContext))
  }

  private def expectWriteTypeDefn(expectedChildContext: TemplateChildContext = TemplateChildContext(javaScope = JavaScope.Class)): Unit = {
    doWrite(
      """/*
        |*  TYPE DEFINITION
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(TypeDefn), eqTemplateChildContext(expectedChildContext))
  }

  private def expectChildOrdering() = {
    when(javaTemplateChildOrdering.compare(any[Tree], any[Tree]))
      .thenAnswer((tree1: Tree, tree2: Tree) => positionOf(tree1) - positionOf(tree2))
  }

  private def positionOf(tree: Tree) = {
    ChildOrder.zipWithIndex
      .find { case (child, _) => child.structure == tree.structure }
      .map(_._2)
      .getOrElse(Int.MaxValue)
  }
}
