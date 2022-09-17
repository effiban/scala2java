package effiban.scala2java.traversers

import effiban.scala2java.contexts.{TemplateBodyContext, TemplateContext}
import effiban.scala2java.entities.JavaTreeType.JavaTreeType
import effiban.scala2java.entities.TraversalContext.javaScope
import effiban.scala2java.entities.{JavaKeyword, JavaTreeType}
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.matchers.TemplateBodyContextMatcher.eqTemplateBodyContext
import effiban.scala2java.matchers.TreeMatcher.eqTree
import effiban.scala2java.resolvers.JavaInheritanceKeywordResolver
import effiban.scala2java.stubbers.OutputWriterStubber.doWrite
import effiban.scala2java.testsuites.UnitTestSuite
import effiban.scala2java.testtrees.{Selfs, Templates, TypeNames}
import org.mockito.ArgumentMatchers

import scala.meta.{Ctor, Defn, Init, Lit, Name, Pat, Self, Stat, Template, Term, Type}

class TemplateTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val TheNonSkippedInits = List(
    Init(tpe = Type.Name("Parent1"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Parent2"), name = Name.Anonymous(), argss = List())
  )

  private val TheSkippedInits = List(
    Init(tpe = Type.Name("Product"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Serializable"), name = Name.Anonymous(), argss = List()),
    Init(tpe = Type.Name("Enumeration"), name = Name.Anonymous(), argss = List())
  )

  private val NonEmptySelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(Type.Name("SelfType")))

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

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
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


  private val initListTraverser = mock[InitListTraverser]
  private val selfTraverser = mock[SelfTraverser]
  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateBodyTraverser = mock[TemplateBodyTraverser]

  private val templateTraverser = new TemplateTraverserImpl(
    initListTraverser,
    selfTraverser,
    templateBodyTraverser,
    javaInheritanceKeywordResolver
  )

  test("traverse when empty") {

    expectWriteSelf()
    expectTraverseBody(stats = Nil)

    templateTraverser.traverse(template = Templates.Empty, context = TemplateContext(javaScope = JavaTreeType.Class))

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListTraverser)
  }

  test("traverse when has inits only, nothing to skip") {
    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectWriteSelf()
    expectWriteInits(JavaTreeType.Class)
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(javaScope = JavaTreeType.Class, inits = TheNonSkippedInits))

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaTreeType.Class))

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse when has inits only and some should be skipped") {
    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits ++ TheSkippedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectWriteSelf()
    expectWriteInits(JavaTreeType.Class)
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(javaScope = JavaTreeType.Class, inits = TheNonSkippedInits))

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaTreeType.Class))

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse when has self only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = NonEmptySelf,
      stats = Nil
    )

    expectWriteSelf(NonEmptySelf)
    expectTraverseBody(stats = Nil)

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaTreeType.Class))

    outputWriter.toString shouldBe
      """/* extends SelfName: SelfType */ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListTraverser)
  }

  test("traverse when has primary ctor only") {
    val context = TemplateContext(
      javaScope = JavaTreeType.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )

    expectWriteSelf()
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(
      javaScope = JavaTreeType.Class,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor)
    )

    templateTraverser.traverse(
      template = Templates.Empty,
      context = context
    )

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse when has stats only") {
    val stats = List(
      DataMemberDefn,
      MethodDefn,
    )

    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = stats
    )

    expectWriteSelf()
    expectTraverseBody(stats = stats)

    templateTraverser.traverse(template = template, context = TemplateContext(javaScope = JavaTreeType.Class))

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListTraverser)
  }

  test("traverse when has everything") {
    val stats = List(
      MethodDefn,
      DataMemberDefn,
    )

    val template = Template(
      early = Nil,
      inits = TheNonSkippedInits,
      self = NonEmptySelf,
      stats = stats
    )

    val context = TemplateContext(
      javaScope = JavaTreeType.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )

    expectWriteInits(JavaTreeType.Class)
    expectWriteSelf(NonEmptySelf)
    expectTraverseBody(stats = stats, context = TemplateBodyContext(
      javaScope = JavaTreeType.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      inits = TheNonSkippedInits)
    )

    javaScope = JavaTreeType.Class

    templateTraverser.traverse(template = template, context = context)

    outputWriter.toString shouldBe
      """ implements Parent1, Parent2/* extends SelfName: SelfType */ {
        |  /* BODY */
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

  private def expectWriteSelf(self: Self = Selfs.Empty): Unit = {
    val selfStr = self match {
      case slf if slf.structure == Selfs.Empty.structure => ""
      case _ => "/* extends SelfName: SelfType */"
    }
    doWrite(selfStr).when(selfTraverser).traverse(eqTree(self))
  }

  private def expectTraverseBody(stats: List[Stat], context: TemplateBodyContext = TemplateBodyContext(javaScope = JavaTreeType.Class)): Unit = {
    doWrite(
      """ {
        |  /* BODY */
        |}
        |""".stripMargin)
      .when(templateBodyTraverser).traverse(
      eqTreeList(stats),
      eqTemplateBodyContext(context))
  }
}
