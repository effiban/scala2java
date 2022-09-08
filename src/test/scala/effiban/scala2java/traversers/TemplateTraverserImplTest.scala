package effiban.scala2java.traversers

import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{ClassInfo, JavaKeyword, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.{eqSomeTree, eqTreeList}
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.orderings.JavaTemplateChildOrdering
import effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{Selfs, Templates, TypeNames}
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any

import scala.meta.{Ctor, Decl, Defn, Init, Lit, Name, Pat, Self, Template, Term, Tree, Type}

class TemplateTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheNonSkippedInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheSkippedInits = List(
    Init(tpe = Type.Name("Product"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Serializable"), name = Name.Anonymous(), argss = List())
  )

  private val TheSelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(Type.Name("SelfType")))

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


  private val initListTraverser = mock[InitListTraverser]
  private val selfTraverser = mock[SelfTraverser]
  private val javaTemplateChildOrdering = mock[JavaTemplateChildOrdering]
  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateChildTraverser = mock[TemplateChildTraverser]

  private val templateTraverser = new TemplateTraverserImpl(
    initListTraverser,
    selfTraverser,
    templateChildTraverser,
    javaTemplateChildOrdering,
    javaInheritanceKeywordResolver
  )

  test("traverse when empty") {

    templateTraverser.traverse(Templates.Empty)

    outputWriter.toString shouldBe
      """ {
        |}
        |""".stripMargin
  }

  test("traverse when has 'self' only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = TheSelf,
      stats = Nil
    )

    expectWriteSelf()

    templateTraverser.traverse(template)

    outputWriter.toString shouldBe
      """/* extends SelfName: SelfType */ {
        |}
        |""".stripMargin
  }

  test("traverse when has inits only, nothing to skip") {
    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits,
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = Nil
    )

    expectWriteInits(JavaTreeType.Class)

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(template)

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
        |}
        |""".stripMargin
  }

  test("traverse when has inits only and some should be skipped") {
    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits ++ TheSkippedInits,
      self = Self(name = Name.Anonymous(), decltpe = None),
      stats = Nil
    )

    expectWriteInits(JavaTreeType.Class)

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(template)

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
        |}
        |""".stripMargin
  }

  test("traverse when has primary ctor. only") {
    expectWritePrimaryCtor()

    expectChildOrdering()

    templateTraverser.traverse(
      template = Templates.Empty,
      maybeClassInfo = Some(ClassInfo(className = ClassName, maybePrimaryCtor = Some(PrimaryCtor)))
    )

    outputWriter.toString shouldBe
      """ {
        |/*
        |*  PRIMARY CTOR
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has primary ctor. and inits only") {
    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectWriteInits(JavaTreeType.Class)
    expectWritePrimaryCtor(TheNonSkippedInits)
    expectChildOrdering()

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(
      template = template,
      maybeClassInfo = Some(ClassInfo(className = ClassName, maybePrimaryCtor = Some(PrimaryCtor)))
    )

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
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
      SecondaryCtor
    )

    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = stats
    )

    expectWriteDataMemberDecl()
    expectWriteDataMemberDefn()
    expectWriteSecondaryCtor()
    expectWriteMethodDefn()

    expectChildOrdering()

    templateTraverser.traverse(
      template = template,
      maybeClassInfo = Some(ClassInfo(className = ClassName, maybePrimaryCtor = None))
    )

    outputWriter.toString shouldBe
      """ {
        |/* DATA MEMBER DECL */;
        |/* DATA MEMBER DEFINITION */;
        |/*
        |*  SECONDARY CTOR
        |*/
        |/*
        |*  METHOD DEFINITION
        |*/
        |}
        |""".stripMargin
  }

  test("traverse when has everything") {
    val stats = List(
      DataMemberDecl,
      MethodDefn,
      DataMemberDefn,
      SecondaryCtor
    )

    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits,
      self = TheSelf,
      stats = stats
    )

    expectWriteInits(JavaTreeType.Class)
    expectWriteSelf()
    expectWriteDataMemberDecl(TheNonSkippedInits)
    expectWriteDataMemberDefn(TheNonSkippedInits)
    expectWritePrimaryCtor(TheNonSkippedInits)
    expectWriteSecondaryCtor(TheNonSkippedInits)
    expectWriteMethodDefn(TheNonSkippedInits)

    expectChildOrdering()

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(
      template = template,
      maybeClassInfo = Some(ClassInfo(className = ClassName, maybePrimaryCtor = Some(PrimaryCtor)))
    )

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2/* extends SelfName: SelfType */ {
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

  private def expectWriteInits(javaScope: JavaTreeType): Unit = {
    when(javaInheritanceKeywordResolver.resolve(ArgumentMatchers.eq(javaScope), eqTreeList(TheNonSkippedInits))).thenReturn(JavaKeyword.Implements)
    doWrite("Parent1, Parent2").when(initListTraverser).traverse(eqTreeList(TheNonSkippedInits), ArgumentMatchers.eq(true))
  }

  private def expectWriteSelf(): Unit = {
    doWrite("/* extends SelfName: SelfType */").when(selfTraverser).traverse(eqTree(TheSelf))
  }

  private def expectWriteDataMemberDecl(inits: List[Init] = Nil): Unit = {
    doWrite(
      """/* DATA MEMBER DECL */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDecl), eqTreeList(inits), eqSomeTree(ClassName))
  }

  private def expectWriteDataMemberDefn(inits: List[Init] = Nil): Unit = {
    doWrite(
    """/* DATA MEMBER DEFINITION */;
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(DataMemberDefn), eqTreeList(inits), eqSomeTree(ClassName))
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

  private def expectWriteMethodDefn(inits: List[Init] = Nil): Unit = {
    doWrite(
      """/*
        |*  METHOD DEFINITION
        |*/
        |""".stripMargin)
      .when(templateChildTraverser).traverse(eqTree(MethodDefn), eqTreeList(inits), eqSomeTree(ClassName))
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
