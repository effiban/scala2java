package effiban.scala2java.traversers

import effiban.scala2java.contexts.TemplateContext
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{CtorContext, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqOptionCtorContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.TypeNames
import org.mockito.ArgumentMatchers.any

import scala.meta.{Ctor, Decl, Defn, Init, Lit, Name, Pat, Term, Tree, Type}

class TemplateBodyTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val CtorContextWithoutTerms = CtorContext(className = ClassName, inits = TheInits)

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
    MethodDefn
  )


  private val javaTemplateChildOrdering = mock[JavaTemplateChildOrdering]
  private val templateChildTraverser = mock[TemplateChildTraverser]

  private val templateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildTraverser,
    javaTemplateChildOrdering,
  )

  test("traverse when empty") {

    templateBodyTraverser.traverse(stats = Nil, inits = Nil)

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has inits only") {
    templateBodyTraverser.traverse(stats = Nil, inits = TheInits)

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has primary ctor. only") {
    val context = TemplateContext(maybeClassName = Some(ClassName), maybePrimaryCtor = Some(PrimaryCtor))

    expectWritePrimaryCtor(Some(CtorContext(className = ClassName, inits = Nil)))

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = Nil, inits = Nil, context = context)

    outputWriter.toString shouldBe
      """ {
        |/*
        |*  PRIMARY CTOR
        |*/
        |}
        |""".stripMargin
  }


  test("traverse when has stats only") {
    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
    )

    expectWriteDataMemberDecl()
    expectWriteDataMemberDefn()
    expectWriteMethodDefn()

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = stats, inits = Nil)

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
    val context = TemplateContext(maybeClassName = Some(ClassName), maybePrimaryCtor = Some(PrimaryCtor))

    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor
    )

    expectWriteDataMemberDecl(Some(CtorContextWithoutTerms))
    expectWriteDataMemberDefn(Some(CtorContextWithoutTerms))
    expectWritePrimaryCtor(Some(CtorContextWithoutTerms))
    expectWriteSecondaryCtor(Some(CtorContextWithoutTerms))
    expectWriteMethodDefn(Some(CtorContextWithoutTerms))

    expectChildOrdering()

    javaScope = JavaTreeType.Class

    templateBodyTraverser.traverse(stats = stats, inits = TheInits, context = context)

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
    val context = TemplateContext(maybeClassName = Some(ClassName), maybePrimaryCtor = Some(PrimaryCtor))

    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor,
      TermApply1,
      TermApply2
    )

    val expectedCtorContext = CtorContext(className = ClassName, inits = TheInits, terms = List(TermApply1, TermApply2))

    expectWriteDataMemberDecl(Some(expectedCtorContext))
    expectWriteDataMemberDefn(Some(expectedCtorContext))
    expectWritePrimaryCtor(Some(expectedCtorContext))
    expectWriteSecondaryCtor(Some(expectedCtorContext))
    expectWriteMethodDefn(Some(expectedCtorContext))

    expectChildOrdering()

    javaScope = JavaTreeType.Class

    templateBodyTraverser.traverse(stats = stats, inits = TheInits, context = context)

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

  private def expectWriteDataMemberDecl(maybeCtorContext: Option[CtorContext] = None): Unit = {
    doWrite(
      """/* DATA MEMBER DECL */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDecl), eqOptionCtorContext(maybeCtorContext))
  }

  private def expectWriteDataMemberDefn(maybeCtorContext: Option[CtorContext] = None): Unit = {
    doWrite(
    """/* DATA MEMBER DEFINITION */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDefn), eqOptionCtorContext(maybeCtorContext))
  }

  private def expectWritePrimaryCtor(maybeCtorContext: Option[CtorContext] = None): Unit = {
    doWrite(
      """/*
        |*  PRIMARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(PrimaryCtor), eqOptionCtorContext(maybeCtorContext))
  }

  private def expectWriteSecondaryCtor(maybeCtorContext: Option[CtorContext] = None): Unit = {
    doWrite(
      """/*
        |*  SECONDARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(SecondaryCtor), eqOptionCtorContext(maybeCtorContext))
  }

  private def expectWriteMethodDefn(maybeCtorContext: Option[CtorContext] = None): Unit = {
    doWrite(
      """/*
        |*  METHOD DEFINITION
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(MethodDefn), eqOptionCtorContext(maybeCtorContext))
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
