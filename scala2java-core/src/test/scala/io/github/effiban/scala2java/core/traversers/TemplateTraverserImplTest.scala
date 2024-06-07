package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.entities.JavaKeyword.Implements
import io.github.effiban.scala2java.core.matchers.TemplateBodyContextMatcher.eqTemplateBodyContext
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Selfs, Templates}
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Init, Name, Self, Stat, Template, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

class TemplateTraverserImplTest extends UnitTestSuite {

  private val ClassName = Type.Name("MyClass")

  private val Init1 = init"Parent1()"
  private val Init2 = init"Parent2()"
  private val Inits = List(Init1, Init2)

  private val TraversedInit1 = init"TraversedParent1()"
  private val TraversedInit2 = init"TraversedParent2()"
  private val TraversedInits = List(TraversedInit1, TraversedInit2)

  private val NonEmptySelf = Self(name = Name.Indeterminate("SelfName"), decltpe = Some(t"SelfType"))
  private val TraversedNonEmptySelf = Self(name = Name.Indeterminate("TraversedSelfName"), decltpe = Some(t"SelfType"))

  private val DefnVar = q"var y = 4"
  private val TraversedDefnVar = q"var yy = 44"

  private val PrimaryCtorArgs = List(param"arg1: Int", param"arg2: String")

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val DefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TraversedDefnDef = q"def myTraversedMethod(param2: Int): Int = doSomething(param2)"

  private val templateInitTraverser = mock[TemplateInitTraverser]
  private val selfTraverser = mock[SelfTraverser]
  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateBodyTraverser = mock[TemplateBodyTraverser]

  private val templateTraverser = new TemplateTraverserImpl(
    templateInitTraverser,
    selfTraverser,
    templateBodyTraverser
  )

  test("traverse when empty") {
    expectTraverseSelf()
    expectTraverseBody()

    val actualTraversedTemplate = templateTraverser.traverse(template = Templates.Empty, context = TemplateContext(JavaScope.Class))
    actualTraversedTemplate.structure shouldBe Templates.Empty.structure
  }

  test("traverse when has inits only, nothing to skip") {
    val template = Template(
      early = Nil,
      inits = Inits,
      self = Selfs.Empty,
      stats = Nil
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = TraversedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseSelf()
    expectTraverseBody(context = TemplateBodyContext(javaScope = JavaScope.Class, inits = Inits))

    val actualTraversedTemplate = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversedTemplate.structure shouldBe expectedTraversedTemplate.structure
  }

  test("traverse when has inits only") {
    val template = Template(
      early = Nil,
      inits = Inits,
      self = Selfs.Empty,
      stats = Nil
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = TraversedInits,
      self = Selfs.Empty,
      stats = Nil
    )

    expectTraverseSelf()
    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseBody(context = TemplateBodyContext(javaScope = JavaScope.Class, inits = Inits))

    val actualTraversedTemplate = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversedTemplate.structure shouldBe expectedTraversedTemplate.structure
  }

  test("traverse when has self only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = NonEmptySelf,
      stats = Nil
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = TraversedNonEmptySelf,
      stats = Nil
    )

    expectTraverseSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectTraverseBody()

    val actualTraversedTemplate = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversedTemplate.structure shouldBe expectedTraversedTemplate.structure
  }

  test("traverse when has primary ctor only") {
    val context = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )

    expectTraverseSelf()
    expectTraverseBody(context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor)
    )

    val actualTraversedTemplate = templateTraverser.traverse(template = Templates.Empty, context = context)
    actualTraversedTemplate.structure shouldBe Templates.Empty.structure
  }

  test("traverse when has stats only") {
    val stats = List(
      DefnVar,
      DefnDef,
    )
    val expectedStats = List(
      TraversedDefnVar,
      TraversedDefnDef
    )

    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = stats
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = expectedStats
    )

    expectTraverseSelf()
    expectTraverseBody(stats = stats, expectedStats = expectedStats)

    val actualTraversedTemplate = templateTraverser.traverse(template = template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversedTemplate.structure shouldBe expectedTraversedTemplate.structure
  }

  test("traverse when has everything") {
    val stats = List(
      DefnDef,
      DefnVar,
    )
    val expectedStats = List(
      TraversedDefnVar,
      TraversedDefnDef
    )

    val template = Template(
      early = Nil,
      inits = Inits,
      self = NonEmptySelf,
      stats = stats
    )
    val expectedTraversedTemplate = Template(
      early = Nil,
      inits = TraversedInits,
      self = TraversedNonEmptySelf,
      stats = expectedStats
    )

    val context = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
    )
    val expectedBodyContext = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor),
      inits = Inits)

    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectTraverseBody(
      stats = stats,
      context = expectedBodyContext,
      expectedStats = expectedStats
    )

    val actualTraversedTemplate = templateTraverser.traverse(template = template, context = context)
    actualTraversedTemplate.structure shouldBe expectedTraversedTemplate.structure
  }

  private def expectResolveInheritanceKeywordAndTraverseInits(javaScope: JavaScope): Unit = {
    expectResolveInheritanceKeyword(javaScope)
    doAnswer((init: Init) => init match {
      case anInit if anInit.structure == Init1.structure => TraversedInit1
      case anInit if anInit.structure == Init2.structure => TraversedInit2
      case anInit => anInit
    }).when(templateInitTraverser).traverse(any[Init])
  }

  private def expectResolveInheritanceKeyword(javaScope: JavaScope) = {
    when(javaInheritanceKeywordResolver.resolve(eqTo(javaScope), eqTreeList(Inits))).thenReturn(Implements)
  }

  private def expectTraverseSelf(self: Self = Selfs.Empty, traversedSelf: Self = Selfs.Empty): Unit = {
    doReturn(traversedSelf).when(selfTraverser).traverse(eqTree(self))
  }

  private def expectTraverseBody(stats: List[Stat] = Nil,
                                 context: TemplateBodyContext = TemplateBodyContext(javaScope = JavaScope.Class),
                                 expectedStats: List[Stat] = Nil): Unit = {
    doReturn(expectedStats)
      .when(templateBodyTraverser).traverse(eqTreeList(stats), eqTemplateBodyContext(context))
  }
}
