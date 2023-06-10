package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, TermParamListRenderContext, TermParamRenderContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.JavaModifier.Final
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.captor.ArgCaptor

import scala.meta.{Name, Term}

class TermParamListRendererImplTest extends UnitTestSuite {
  private val ExpectedOptionsForMultiLine = ListTraversalOptions(
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val ExpectedOptionsForSameLine = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val argumentListRenderer = mock[ArgumentListRenderer]
  private val termParamArgRendererFactory = mock[TermParamArgRendererFactory]

  private val argRendererProviderCaptor = ArgCaptor[Int => ArgumentRenderer[Term.Param]]

  private val termParamListRenderer = new TermParamListRendererImpl(argumentListRenderer, termParamArgRendererFactory)


  test("render() when no params") {
    val paramListContext = TermParamListRenderContext()
    termParamListRenderer.render(termParams = Nil, context = paramListContext)

    verifyArgumentListRendererInvocation(Nil, paramListContext, ExpectedOptionsForMultiLine)
  }

  test("render() when one param and multi-line") {
    val param = termParam("x")
    val paramContext = TermParamRenderContext(List(Final))
    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext))

    termParamListRenderer.render(termParams = List(param), context = paramListContext)

    verifyArgumentListRendererInvocation(List(param), paramListContext, ExpectedOptionsForMultiLine)
  }

  test("render() when one param and same line") {
    val param = termParam("x")
    val paramContext = TermParamRenderContext(List(Final))
    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext), onSameLine = true)

    termParamListRenderer.render(termParams = List(param), context = paramListContext)

    verifyArgumentListRendererInvocation(List(param), paramListContext, ExpectedOptionsForSameLine)
  }

  test("render() when two terms and multi-line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)
    val paramContext1 = TermParamRenderContext(List(Final))
    val paramContext2 = TermParamRenderContext()
    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext1, paramContext2))

    termParamListRenderer.render(termParams = params, context = paramListContext)

    verifyArgumentListRendererInvocation(params, paramListContext, ExpectedOptionsForMultiLine)
  }

  test("render() when two terms and same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)
    val paramContext1 = TermParamRenderContext(List(Final))
    val paramContext2 = TermParamRenderContext()
    val paramListContext = TermParamListRenderContext(paramContexts = List(paramContext1, paramContext2), onSameLine = true)

    termParamListRenderer.render(termParams = params, context = paramListContext)

    verifyArgumentListRendererInvocation(params, paramListContext, ExpectedOptionsForSameLine)
  }

  private def verifyArgumentListRendererInvocation(args: List[Term.Param],
                                                   paramListRenderContext: TermParamListRenderContext,
                                                   options: ListTraversalOptions): Unit = {
    verify(argumentListRenderer).render(
      args = eqTreeList(args),
      argRendererProvider = argRendererProviderCaptor,
      context = eqArgumentListContext(ArgumentListContext(options = options))
    )
    argRendererProviderCaptor.value(0)
    verify(termParamArgRendererFactory).create(eqTo(paramListRenderContext), eqTo(0))
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }

}
