package effiban.scala2java.traversers

import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.{eqOptionTree, eqSomeTree, eqTreeList}
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

  private val Statement = Term.Apply(fun = Term.Name("doSomething"), args = List(Term.Name("param1")))

  private val MethodDefn = Defn.Def(
    mods = Nil,
    name = Term.Name("myMethod"),
    tparams = Nil,
    paramss = List(List(termParam("param", "Int"))),
    decltpe = Some(TypeNames.Int),
    body = Statement
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

    templateBodyTraverser.traverse(stats = Nil, inits = Nil, maybeClassInfo = None)

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has inits only") {
    templateBodyTraverser.traverse(stats = Nil, inits = TheInits, maybeClassInfo = None)

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has primary ctor. only") {
    val classInfo = ClassInfo(className = ClassName, maybePrimaryCtor = Some(PrimaryCtor))

    expectWritePrimaryCtor()

    expectChildOrdering()

    templateBodyTraverser.traverse(stats = Nil, inits = Nil, maybeClassInfo = Some(classInfo))

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

    templateBodyTraverser.traverse(stats = stats, inits = Nil, maybeClassInfo = None)

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

  test("traverse when has everything") {
    val classInfo = ClassInfo(className = ClassName, maybePrimaryCtor = Some(PrimaryCtor))

    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor
    )

    expectWriteDataMemberDecl(TheInits, Some(ClassName))
    expectWriteDataMemberDefn(TheInits, Some(ClassName))
    expectWritePrimaryCtor(TheInits)
    expectWriteSecondaryCtor(TheInits)
    expectWriteMethodDefn(TheInits, Some(ClassName))

    expectChildOrdering()

    javaScope = JavaTreeType.Class

    templateBodyTraverser.traverse(stats = stats, inits = TheInits, maybeClassInfo = Some(classInfo))

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

  private def expectWriteDataMemberDecl(inits: List[Init] = Nil, maybeClassName: Option[Type.Name] = None): Unit = {
    doWrite(
      """/* DATA MEMBER DECL */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDecl), eqTreeList(inits), eqOptionTree(maybeClassName))
  }

  private def expectWriteDataMemberDefn(inits: List[Init] = Nil, maybeClassName: Option[Type.Name] = None): Unit = {
    doWrite(
    """/* DATA MEMBER DEFINITION */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDefn), eqTreeList(inits), eqOptionTree(maybeClassName))
  }

  private def expectWritePrimaryCtor(inits: List[Init] = Nil): Unit = {
    doWrite(
      """/*
        |*  PRIMARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(PrimaryCtor), eqTreeList(inits), eqSomeTree(ClassName))
  }

  private def expectWriteSecondaryCtor(inits: List[Init] = Nil): Unit = {
    doWrite(
      """/*
        |*  SECONDARY CTOR
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(SecondaryCtor), eqTreeList(inits), eqSomeTree(ClassName))
  }

  private def expectWriteMethodDefn(inits: List[Init] = Nil, maybeClassName: Option[Type.Name] = None): Unit = {
    doWrite(
      """/*
        |*  METHOD DEFINITION
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(MethodDefn), eqTreeList(inits), eqOptionTree(maybeClassName))
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
