package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentListContext, StatContext}
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.ArgumentListContextMatcher.eqArgumentListContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import io.github.effiban.scala2java.test.utils.matchers.CombinedMatchers.eqTreeList
import org.mockito.ArgumentMatchersSugar.eqTo

import scala.meta.{Name, Term}

class DeprecatedTermParamListTraverserImplTest extends UnitTestSuite {

  private val TheStatContext = StatContext(JavaScope.MethodSignature)

  private val ExpectedOptionsForMultiLine = ListTraversalOptions(
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val ExpectedOptionsForSameLine = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val argumentListTraverser = mock[DeprecatedArgumentListTraverser]
  private val termParamArgTraverserFactory = mock[DeprecatedTermParamArgTraverserFactory]
  private val termParamArgTraverser = mock[DeprecatedArgumentTraverser[Term.Param]]

  private val termParamListTraverser = new DeprecatedTermParamListTraverserImpl(argumentListTraverser, termParamArgTraverserFactory)


  test("traverse() when no params") {
    when(termParamArgTraverserFactory(TheStatContext)).thenReturn(termParamArgTraverser)

    termParamListTraverser.traverse(termParams = Nil, context = TheStatContext)

    verifyArgumentListTraverserInvocation(Nil, ExpectedOptionsForMultiLine)
  }

  test("traverse() when one param and multi-line") {
    val param = termParam("x")

    when(termParamArgTraverserFactory(TheStatContext)).thenReturn(termParamArgTraverser)

    termParamListTraverser.traverse(termParams = List(param), context = TheStatContext)

    verifyArgumentListTraverserInvocation(List(param), ExpectedOptionsForMultiLine)
  }

  test("traverse() when one param and same line") {
    val param = termParam("x")

    when(termParamArgTraverserFactory(TheStatContext)).thenReturn(termParamArgTraverser)

    termParamListTraverser.traverse(termParams = List(param), context = TheStatContext, onSameLine = true)

    verifyArgumentListTraverserInvocation(List(param), ExpectedOptionsForSameLine)
  }

  test("traverse() when two terms and multi-line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)

    when(termParamArgTraverserFactory(TheStatContext)).thenReturn(termParamArgTraverser)

    termParamListTraverser.traverse(termParams = params, context = TheStatContext)

    verifyArgumentListTraverserInvocation(params, ExpectedOptionsForMultiLine)
  }

  test("traverse() when two terms and same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)

    when(termParamArgTraverserFactory(TheStatContext)).thenReturn(termParamArgTraverser)

    termParamListTraverser.traverse(termParams = params, context = TheStatContext, onSameLine = true)

    verifyArgumentListTraverserInvocation(params, ExpectedOptionsForSameLine)
  }

  private def verifyArgumentListTraverserInvocation(args: List[Term.Param], options: ListTraversalOptions): Unit = {
    verify(argumentListTraverser).traverse(
      args = eqTreeList(args),
      argTraverser = eqTo(termParamArgTraverser),
      context = eqArgumentListContext(ArgumentListContext(options = options))
    )
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }
}
