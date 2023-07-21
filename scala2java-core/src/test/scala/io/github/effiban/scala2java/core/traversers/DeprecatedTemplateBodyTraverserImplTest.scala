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

@deprecated
class DeprecatedTemplateBodyTraverserImplTest extends UnitTestSuite {

  private val TermApply1A = q"doSomething1(param1A)"
  private val TermApply1B = q"doSomething1(param1B)"
  private val TermApply2A = q"doSomething2(param2A)"
  private val TermApply2B = q"doSomething2(param2B)"
  private val TermApply3A = q"doSomething3(param3A)"
  private val TermApply3B = q"doSomething3(param3B)"

  private val DefnVal1A = q"""val first = "A""""
  private val DefnVal1B = q"""val first = "B""""
  private val DefnVal2A = q"""val second = "A""""
  private val DefnVal2B = q"""val second = "B""""
  private val DefnVal3A = q"""val third = "A""""
  private val DefnVal3B = q"""val third = "B""""

  private val templateChildrenTraverser = mock[DeprecatedTemplateChildrenTraverser]
  private val templateStatTransformer = mock[TemplateStatTransformer]
  private val templateChildrenResolver = mock[TemplateChildrenResolver]
  private val templateChildContextFactory = mock[TemplateChildContextFactory]

  private val bodyContext = mock[TemplateBodyContext]
  private val childContext = mock[TemplateChildContext]

  private val templateBodyTraverser = new DeprecatedTemplateBodyTraverserImpl(
    templateChildrenTraverser,
    templateStatTransformer,
    templateChildrenResolver,
    templateChildContextFactory
  )


  test("traverse when empty") {
    when(templateChildrenResolver.resolve(terms = eqTo(Nil), nonTerms = eqTo(Nil), eqTo(bodyContext))).thenReturn(Nil)
    when(templateChildContextFactory.create(bodyContext, Nil)).thenReturn(childContext)

    templateBodyTraverser.traverse(Nil, bodyContext)

    verify(templateChildrenTraverser).traverse(Nil, childContext)
  }

  test("traverse when has terms only") {
    val terms = List(TermApply1A, TermApply2A, TermApply3A)
    val transformedTerms = List(TermApply1B, TermApply2B, TermApply3B)
    val children = List(TermApply1B, TermApply2B)

    expectTransformStats(terms, transformedTerms)
    when(templateChildrenResolver.resolve(terms = eqTreeList(transformedTerms), nonTerms = eqTo(Nil), eqTo(bodyContext)))
      .thenReturn(children)
    when(templateChildContextFactory.create(eqTo(bodyContext), eqTreeList(transformedTerms))).thenReturn(childContext)

    templateBodyTraverser.traverse(terms, bodyContext)

    verify(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))
  }

  test("traverse when has non-terms only") {
    val defns = List(DefnVal1A, DefnVal2A, DefnVal3A)
    val transformedDefns = List(DefnVal1B, DefnVal2B, DefnVal3B)
    val children = List(DefnVal1B, DefnVal2B)

    expectTransformStats(defns, transformedDefns)
    when(templateChildrenResolver.resolve(terms = eqTo(Nil), nonTerms = eqTreeList(transformedDefns), eqTo(bodyContext)))
      .thenReturn(children)
    when(templateChildContextFactory.create(bodyContext, Nil)).thenReturn(childContext)

    templateBodyTraverser.traverse(defns, bodyContext)

    verify(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))
  }

  test("traverse when has terms and non-terms") {
    val terms = List(TermApply1A, TermApply2A, TermApply3A)
    val transformedTerms = List(TermApply1B, TermApply2B, TermApply3B)

    val defns = List(DefnVal1A, DefnVal2A, DefnVal3A)
    val transformedDefns = List(DefnVal1B, DefnVal2B, DefnVal3B)

    val stats = terms ++ defns
    val transformedStats = transformedTerms ++ transformedDefns

    val children = List(TermApply1B, TermApply2B, DefnVal1B, DefnVal2B)

    expectTransformStats(stats, transformedStats)
    when(templateChildrenResolver.resolve(eqTreeList(transformedTerms), eqTreeList(transformedDefns), eqTo(bodyContext))).thenReturn(children)
    when(templateChildContextFactory.create(eqTo(bodyContext), eqTreeList(transformedTerms))).thenReturn(childContext)

    templateBodyTraverser.traverse(stats, bodyContext)

    verify(templateChildrenTraverser).traverse(eqTreeList(children), eqTo(childContext))
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
