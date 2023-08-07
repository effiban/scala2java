package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{TemplateBodyContext, TemplateChildContext}
import io.github.effiban.scala2java.core.factories.TemplateChildContextFactory
import io.github.effiban.scala2java.core.resolvers.TemplateChildrenResolver
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.transformers.TemplateStatTransformer
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Stat, XtensionQuasiquoteTerm}

class TemplateBodyTraverserImplTest extends UnitTestSuite {

  private val TermApply1A = q"doSomething1(param1A)"
  private val TermApply1B = q"doSomething1(param1B)"
  private val TermApply1C = q"doSomething1(param1C)"

  private val TermApply2A = q"doSomething2(param2A)"
  private val TermApply2B = q"doSomething2(param2B)"
  private val TermApply2C = q"doSomething2(param2C)"

  private val TermApply3A = q"doSomething3(param3A)"
  private val TermApply3B = q"doSomething3(param3B)"

  private val DefnVar1A = q"""var first = "A""""
  private val DefnVar1B = q"""var first = "B""""
  private val DefnVar1C = q"""var first = "C""""

  private val DefnVar2A = q"""var second = "A""""
  private val DefnVar2B = q"""var second = "B""""
  private val DefnVar2C = q"""var second = "C""""

  private val DefnVar3A = q"""var third = "A""""
  private val DefnVar3B = q"""var third = "B""""

  private val templateChildrenTraverser = mock[TemplateChildrenTraverser]
  private val templateStatTransformer = mock[TemplateStatTransformer]
  private val templateChildrenResolver = mock[TemplateChildrenResolver]
  private val templateChildContextFactory = mock[TemplateChildContextFactory]

  private val bodyContext = mock[TemplateBodyContext]
  private val childContext = mock[TemplateChildContext]

  private val templateBodyTraverser = new TemplateBodyTraverserImpl(
    templateChildrenTraverser,
    templateStatTransformer,
    templateChildrenResolver,
    templateChildContextFactory
  )


  test("traverse when empty") {
    when(templateChildrenResolver.resolve(terms = eqTo(Nil), nonTerms = eqTo(Nil), eqTo(bodyContext))).thenReturn(Nil)
    when(templateChildContextFactory.create(bodyContext, Nil)).thenReturn(childContext)
    doReturn(Nil).when(templateChildrenTraverser).traverse(Nil, childContext)

    templateBodyTraverser.traverse(Nil, bodyContext) shouldBe Nil
  }

  test("traverse when has terms only") {
    val terms = List(TermApply1A, TermApply2A, TermApply3A)
    val transformedTerms = List(TermApply1B, TermApply2B, TermApply3B)
    val children = List(TermApply1B, TermApply2B)
    val expectedTraversedChildren = List(TermApply1C, TermApply2C)

    expectTransformStats(terms, transformedTerms)
    when(templateChildrenResolver.resolve(terms = eqTreeList(transformedTerms), nonTerms = eqTo(Nil), eqTo(bodyContext)))
      .thenReturn(children)
    when(templateChildContextFactory.create(eqTo(bodyContext), eqTreeList(transformedTerms))).thenReturn(childContext)
    doReturn(expectedTraversedChildren).when(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))

    templateBodyTraverser.traverse(terms, bodyContext).structure shouldBe expectedTraversedChildren.structure
  }

  test("traverse when has non-terms only") {
    val defns = List(DefnVar1A, DefnVar2A, DefnVar3A)
    val transformedDefns = List(DefnVar1B, DefnVar2B, DefnVar3B)
    val children = List(DefnVar1B, DefnVar2B)
    val expectedTraversedChildren = List(DefnVar1C, DefnVar2C)

    expectTransformStats(defns, transformedDefns)
    when(templateChildrenResolver.resolve(terms = eqTo(Nil), nonTerms = eqTreeList(transformedDefns), eqTo(bodyContext)))
      .thenReturn(children)
    when(templateChildContextFactory.create(bodyContext, Nil)).thenReturn(childContext)
    doReturn(expectedTraversedChildren).when(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))

    templateBodyTraverser.traverse(defns, bodyContext).structure shouldBe expectedTraversedChildren.structure
  }

  test("traverse when has terms and non-terms") {
    val terms = List(TermApply1A, TermApply2A, TermApply3A)
    val transformedTerms = List(TermApply1B, TermApply2B, TermApply3B)

    val defns = List(DefnVar1A, DefnVar2A, DefnVar3A)
    val transformedDefns = List(DefnVar1B, DefnVar2B, DefnVar3B)

    val stats = terms ++ defns
    val transformedStats = transformedTerms ++ transformedDefns

    val children = List(TermApply1B, TermApply2B, DefnVar1B, DefnVar2B)
    val expectedTraversedChildren = List(
      TermApply1C,
      TermApply2C,
      DefnVar1C,
      DefnVar2C
    )

    expectTransformStats(stats, transformedStats)
    when(templateChildrenResolver.resolve(eqTreeList(transformedTerms), eqTreeList(transformedDefns), eqTo(bodyContext))).thenReturn(children)
    when(templateChildContextFactory.create(eqTo(bodyContext), eqTreeList(transformedTerms))).thenReturn(childContext)
    doReturn(expectedTraversedChildren).when(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))

    templateBodyTraverser.traverse(stats, bodyContext).structure shouldBe expectedTraversedChildren.structure
  }

  private def expectTransformStats(stats: List[Stat], transformedStats: List[Stat]): Unit = {
    when(templateStatTransformer.transform(any[Stat])).thenAnswer( (stat: Stat) => {
      stats.zipWithIndex
        .find(statWithIndex => statWithIndex._1.structure == stat.structure)
        .map(statWithIndex => transformedStats(statWithIndex._2)) match {
        case Some(transformedStat) => transformedStat
        case None => throw new IllegalStateException("Received an unexpected mock stat " + stat)
      }
    })
  }
}
