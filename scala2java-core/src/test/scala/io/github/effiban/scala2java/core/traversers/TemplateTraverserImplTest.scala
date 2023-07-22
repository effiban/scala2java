package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateContext}
import io.github.effiban.scala2java.core.entities.JavaKeyword.Implements
import io.github.effiban.scala2java.core.matchers.TemplateBodyContextMatcher.eqTemplateBodyContext
import io.github.effiban.scala2java.core.resolvers.JavaInheritanceKeywordResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.testtrees.{Selfs, Templates}
import io.github.effiban.scala2java.core.traversers.results.matchers.TemplateTraversalResultScalatestMatcher.equalTemplateTraversalResult
import io.github.effiban.scala2java.core.traversers.results._
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.spi.predicates.TemplateInitExcludedPredicate
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Ctor, Init, Name, Self, Stat, Template, Type, XtensionQuasiquoteInit, XtensionQuasiquoteTerm, XtensionQuasiquoteTermParam, XtensionQuasiquoteType}

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

  private val DefnVar = q"var y = 4"
  private val TraversedDefnVar = q"var yy = 44"
  private val TheDefnVarTraversalResult = DefnVarTraversalResult(TraversedDefnVar)

  private val PrimaryCtorArgs = List(param"arg1: Int", param"arg2: String")

  private val PrimaryCtor = Ctor.Primary(
    mods = Nil,
    name = Name.Anonymous(),
    paramss = List(PrimaryCtorArgs)
  )

  private val DefnDef = q"def myMethod(param: Int): Int = doSomething(param)"
  private val TraversedDefnDef = q"def myTraversedMethod(param2: Int): Int = doSomething(param2)"
  private val TheDefnDefTraversalResult = DefnDefTraversalResult(TraversedDefnDef)

  private val initTraverser = mock[InitTraverser]
  private val selfTraverser = mock[SelfTraverser]
  private val javaInheritanceKeywordResolver = mock[JavaInheritanceKeywordResolver]
  private val templateBodyTraverser = mock[TemplateBodyTraverser]
  private val templateInitExcludedPredicate = mock[TemplateInitExcludedPredicate]

  private val templateTraverser = new TemplateTraverserImpl(
    initTraverser,
    selfTraverser,
    templateBodyTraverser,
    javaInheritanceKeywordResolver,
    templateInitExcludedPredicate
  )

  test("traverse when empty") {
    val expectedTraversalResult = TemplateTraversalResult()

    expectTraverseSelf()
    expectTraverseBody()

    val actualTraversalResult = templateTraverser.traverse(template = Templates.Empty, context = TemplateContext(JavaScope.Class))
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has inits only, nothing to skip") {
    val template = Template(
      early = Nil,
      inits = IncludedInits,
      self = Selfs.Empty,
      stats = Nil
    )
    val expectedTraversalResult = TemplateTraversalResult(
      maybeInheritanceKeyword = Some(Implements),
      inits = TraversedIncludedInits,
      self = Selfs.Empty,
      statResults = Nil
    )

    expectFilterInits()
    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseSelf()
    expectTraverseBody(context = TemplateBodyContext(javaScope = JavaScope.Class, inits = IncludedInits))

    val actualTraversalResult = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has inits only and some should be skipped") {
    val template = Template(
      early = Nil,
      inits = IncludedInits ++ ExcludedInits,
      self = Selfs.Empty,
      stats = Nil
    )
    val expectedTraversalResult = TemplateTraversalResult(
      maybeInheritanceKeyword = Some(Implements),
      inits = TraversedIncludedInits
    )

    expectFilterInits()
    expectTraverseSelf()
    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseBody(context = TemplateBodyContext(javaScope = JavaScope.Class, inits = IncludedInits))

    val actualTraversalResult = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has self only") {
    val template = Template(
      early = Nil,
      inits = Nil,
      self = NonEmptySelf,
      stats = Nil
    )
    val expectedTraversalResult = TemplateTraversalResult(self = TraversedNonEmptySelf)

    expectTraverseSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectTraverseBody()

    val actualTraversalResult = templateTraverser.traverse(template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has primary ctor only") {
    val context = TemplateContext(
      javaScope = JavaScope.Class,
      maybeClassName = Some(ClassName),
      maybePrimaryCtor = Some(PrimaryCtor)
    )
    val expectedTraversalResult = TemplateTraversalResult()

    expectTraverseSelf()
    expectTraverseBody(context = TemplateBodyContext(
      javaScope = JavaScope.Class,
      maybeClassName = context.maybeClassName,
      maybePrimaryCtor = context.maybePrimaryCtor)
    )

    val actualTraversalResult = templateTraverser.traverse(template = Templates.Empty, context = context)
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has stats only") {
    val stats = List(
      DefnVar,
      DefnDef,
    )
    val expectedStatResults = List(
      TheDefnVarTraversalResult,
      TheDefnDefTraversalResult
    )

    val template = Template(
      early = Nil,
      inits = Nil,
      self = Selfs.Empty,
      stats = stats
    )
    val expectedTraversalResult = TemplateTraversalResult(statResults = expectedStatResults)

    expectTraverseSelf()
    expectTraverseBody(stats = stats, expectedStatResults = expectedStatResults)

    val actualTraversalResult = templateTraverser.traverse(template = template, context = TemplateContext(javaScope = JavaScope.Class))
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  test("traverse when has everything") {
    val stats = List(
      DefnDef,
      DefnVar,
    )
    val expectedStatResults = List(
      TheDefnVarTraversalResult,
      TheDefnDefTraversalResult
    )

    val template = Template(
      early = Nil,
      inits = IncludedInits,
      self = NonEmptySelf,
      stats = stats
    )
    val expectedTraversalResult = TemplateTraversalResult(
      maybeInheritanceKeyword = Some(Implements),
      inits = TraversedIncludedInits,
      self = TraversedNonEmptySelf,
      statResults = expectedStatResults
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
      inits = IncludedInits)

    expectFilterInits()
    expectResolveInheritanceKeywordAndTraverseInits(JavaScope.Class)
    expectTraverseSelf(NonEmptySelf, TraversedNonEmptySelf)
    expectTraverseBody(
      stats = stats,
      context = expectedBodyContext,
      expectedStatResults = expectedStatResults
    )

    val actualTraversalResult = templateTraverser.traverse(template = template, context = context)
    actualTraversalResult should equalTemplateTraversalResult(expectedTraversalResult)
  }

  private def expectFilterInits(): Unit = {
      when(templateInitExcludedPredicate.apply(any[Init])).thenAnswer(
        (actualInit: Init) => ExcludedInits.exists(_.structure == actualInit.structure)
      )
  }

  private def expectResolveInheritanceKeywordAndTraverseInits(javaScope: JavaScope): Unit = {
    expectResolveInheritanceKeyword(javaScope)
    doAnswer((init: Init) => init match {
      case anInit if anInit.structure == IncludedInit1.structure => TraversedIncludedInit1
      case anInit if anInit.structure == IncludedInit2.structure => TraversedIncludedInit2
      case anInit => anInit
    }).when(initTraverser).traverse(any[Init])
  }

  private def expectResolveInheritanceKeyword(javaScope: JavaScope) = {
    when(javaInheritanceKeywordResolver.resolve(eqTo(javaScope), eqTreeList(IncludedInits))).thenReturn(Implements)
  }

  private def expectTraverseSelf(self: Self = Selfs.Empty, traversedSelf: Self = Selfs.Empty): Unit = {
    doReturn(traversedSelf).when(selfTraverser).traverse(eqTree(self))
  }

  private def expectTraverseBody(stats: List[Stat] = Nil,
                                 context: TemplateBodyContext = TemplateBodyContext(javaScope = JavaScope.Class),
                                 expectedStatResults: List[PopulatedStatTraversalResult] = Nil): Unit = {
    doReturn(MultiStatTraversalResult(expectedStatResults))
      .when(templateBodyTraverser).traverse(
      eqTreeList(stats),
      eqTemplateBodyContext(context))
  }
}
