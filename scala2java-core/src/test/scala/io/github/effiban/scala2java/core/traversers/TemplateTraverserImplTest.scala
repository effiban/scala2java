package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{InitContext, TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.entities.JavaKeyword.Implements
import io.github.effiban.scala2java.core.matchers.TemplateBodyContextMatcher.eqTemplateBodyContext
import io.github.effiban.scala2java.core.renderers.{InitListRenderer, PermittedSubTypeNameListRenderer, SelfRenderer}
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.stubbers.OutputWriterStubber.doWrite
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Selfs, Templates, TypeNames}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Defn, Init, Lit, Name, Pat, Self, Stat, Template, Term, Type, XtensionQuasiquoteInit, XtensionQuasiquoteType}

class TemplateTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val IncludedInit1 = init"Parent1()"
  private val IncludedInit2 = init"Parent2()"
  private val IncludedInits = List(IncludedInit1, IncludedInit2)

  private val TraversedIncludedInit1 = init"TraversedParent1()"
  private val TraversedIncludedInit2 = init"TraversedParent2()"
  private val TraversedIncludedInits = List(TraversedIncludedInit1, TraversedIncludedInit2)

  private val ExcludedInits = List(init"Product()", init"Serializable()", init"Enumeration()")

  private val NonEmptySelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))
  private val TraversedNonEmptySelf = Self(name = Name.Indeterminate("TraversedSelfName"), decltpe = Some(t"SelfType"))

  private val PermittedSubTypeNames = List(Type.Name("Child1"), Term.Name("Child2"))

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


  private val initTraverser = mock[InitTraverser]
  private val initListRenderer = mock[InitListRenderer]
  private val selfTraverser = mock[SelfTraverser]
  private val selfRenderer = mock[SelfRenderer]
  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateBodyTraverser = mock[TemplateBodyTraverser]
  private val permittedSubTypeNameListRenderer = mock[PermittedSubTypeNameListRenderer]
  private val templateInitExcludedPredicate = mock[TemplateInitExcludedPredicate]

  private val templateTraverser = new TemplateTraverserImpl(
    initTraverser,
    initListRenderer,
    selfTraverser,
    selfRenderer,
    templateBodyTraverser,
    permittedSubTypeNameListRenderer,
    javaInheritanceKeywordResolver,
    templateInitExcludedPredicate
  )

  test("traverse when empty") {

    expectWriteSelf()
    expectTraverseBody(stats = Nil)

    templateTraverser.traverse(template = Templates.Empty, context = TemplateContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListRenderer)
  }

  test("traverse when has inits only, nothing to skip") {
    val template = Template(
      early = Nil,
      inits = IncludedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectFilterInits()
    expectWriteInits(JavaScope.Class)
    expectWriteSelf()
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(javaScope = JavaScope.Class, inits = IncludedInits))

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ implements TraversedParent1, TraversedParent2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  test("traverse when has inits only and some should be skipped") {
    val template = Template(
      early = Nil,
      inits = IncludedInits ++ ExcludedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectFilterInits()
    expectWriteSelf()
    expectWriteInits(JavaScope.Class)
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(javaScope = JavaScope.Class, inits = IncludedInits))

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ implements TraversedParent1, TraversedParent2 {
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

    expectWriteSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectTraverseBody(stats = Nil)

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """/* extends SelfName: SelfType */ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListRenderer)
  }

  test("traverse when has permitted subtypes only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = Nil
    )

    expectWriteSelf()
    expectRenderPermittedSubTypeNames()
    expectTraverseBody(stats = Nil)

    templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class, permittedSubTypeNames = PermittedSubTypeNames))

    outputWriter.toString shouldBe
      """ permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListRenderer)
  }

  test("traverse when has primary ctor only") {
    val context = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )

    expectWriteSelf()
    expectTraverseBody(stats = Nil, context = TemplateBodyContext(
      javaScope = JavaScope.Class,
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

    templateTraverser.traverse(template = template, context = TemplateContext(javaScope = JavaScope.Class))

    outputWriter.toString shouldBe
      """ {
        |  /* BODY */
        |}
        |""".stripMargin

    verifyZeroInteractions(initListRenderer)
  }

  test("traverse when has everything") {
    val stats = List(
      MethodDefn,
      DataMemberDefn,
    )

    val template = Template(
      early = Nil,
      inits = IncludedInits,
      self = NonEmptySelf,
      stats = stats
    )

    val context = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      permittedSubTypeNames = PermittedSubTypeNames
    )

    expectFilterInits()
    expectWriteInits(JavaScope.Class)
    expectWriteSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectRenderPermittedSubTypeNames()
    expectTraverseBody(stats = stats, context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      inits = IncludedInits)
    )

    templateTraverser.traverse(template = template, context = context)

    outputWriter.toString shouldBe
      """ implements TraversedParent1, TraversedParent2/* extends SelfName: SelfType */ permits Child1, Child2 {
        |  /* BODY */
        |}
        |""".stripMargin
  }

  private def termParam(name: String, typeName: String) = {
    Term.Param(mods = List(), name = Term.Name(name), decltpe = Some(Type.Name(typeName)), default = None)
  }

  private def expectFilterInits(): Unit = {
      when(templateInitExcludedPredicate.apply(any[Init])).thenAnswer(
        (actualInit: Init) => ExcludedInits.exists(_.structure == actualInit.structure)
      )
  }

  private def expectWriteInits(javaScope: JavaScope): Unit = {
    when(javaInheritanceKeywordResolver.resolve(eqTo(javaScope), eqTreeList(IncludedInits))).thenReturn(Implements)
    doAnswer((init: Init) => init match {
      case anInit if anInit.structure == IncludedInit1.structure => TraversedIncludedInit1
      case anInit if anInit.structure == IncludedInit2.structure => TraversedIncludedInit2
      case anInit => anInit
    }).when(initTraverser).traverse(any[Init])
    doWrite("TraversedParent1, TraversedParent2")
      .when(initListRenderer).render(eqTreeList(TraversedIncludedInits), eqTo(InitContext(ignoreArgs = true)))
  }

  private def expectWriteSelf(self: Self = Selfs.Empty, traversedSelf: Self = Selfs.Empty): Unit = {
    doReturn(traversedSelf).when(selfTraverser).traverse(eqTree(self))
    val selfStr = traversedSelf match {
      case slf if slf.structure == Selfs.Empty.structure => ""
      case _ => "/* extends SelfName: SelfType */"
    }
    doWrite(selfStr).when(selfRenderer).render(eqTree(traversedSelf))
  }

  private def expectRenderPermittedSubTypeNames(): Unit = {
    doWrite("permits Child1, Child2")
      .when(permittedSubTypeNameListRenderer).render(eqTreeList(PermittedSubTypeNames))
  }

  private def expectTraverseBody(stats: List[Stat], context: TemplateBodyContext = TemplateBodyContext(javaScope = JavaScope.Class)): Unit = {
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
