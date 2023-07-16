package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.renderers.contexts.TermParamListRenderContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

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
  private val argumentRenderer = mock[ArgumentRenderer[Term.Param]]

  private val termParamListRenderer = new TermParamListRendererImpl(argumentListRenderer, argumentRenderer)


  test("render() when no params") {
    val paramListContext = TermParamListRenderContext()

    termParamListRenderer.render(termParams = Nil, context = paramListContext)

    verifyArgumentListRendererInvocation(Nil, ExpectedOptionsForMultiLine)
  }

  test("render() when one param and multi-line") {
    val param = termParam("x")

    termParamListRenderer.render(termParams = List(param))

    verifyArgumentListRendererInvocation(List(param), ExpectedOptionsForMultiLine)
  }

  test("render() when one param and same line") {
    val param = termParam("x")
    val paramListContext = TermParamListRenderContext(onSameLine = true)

    termParamListRenderer.render(termParams = List(param), context = paramListContext)

    verifyArgumentListRendererInvocation(List(param), ExpectedOptionsForSameLine)
  }

  test("render() when two params and multi-line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)
    val paramListContext = TermParamListRenderContext()

    termParamListRenderer.render(termParams = params, context = paramListContext)

    verifyArgumentListRendererInvocation(params, ExpectedOptionsForMultiLine)
  }

  test("render() when two params and same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)
    val paramListContext = TermParamListRenderContext(onSameLine = true)

    termParamListRenderer.render(termParams = params, context = paramListContext)

    verifyArgumentListRendererInvocation(params, ExpectedOptionsForSameLine)
  }

  private def verifyArgumentListRendererInvocation(args: List[Term.Param],
                                                   options: ListTraversalOptions): Unit = {
    verify(argumentListRenderer).render(
      args = eqTreeList(args),
      argRenderer = eqTo(argumentRenderer),
      context = eqArgumentListContext(ArgumentListContext(options = options))
    )
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }

}