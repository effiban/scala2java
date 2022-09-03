package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions
import effiban.scala2java.matchers.CombinedMatchers.eqTreeList
import effiban.scala2java.testsuites.UnitTestSuite
import org.mockito.ArgumentMatchers

import scala.meta.{Name, Term}

class TermParamListTraverserImplTest extends UnitTestSuite {

  private val ExpectedOptionsForMultiLine = ListTraversalOptions(
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val ExpectedOptionsForSameLine = ListTraversalOptions(
    onSameLine = true,
    maybeEnclosingDelimiter = Some(Parentheses),
    traverseEmpty = true
  )

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termParamTraverser = mock[TermParamTraverser]

  private val termParamListTraverser = new TermParamListTraverserImpl(argumentListTraverser, termParamTraverser)


  test("traverse() when no params") {
    termParamListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      options = ArgumentMatchers.eq(ExpectedOptionsForMultiLine)
    )
  }

  test("traverse() when one param and multi-line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      options = ArgumentMatchers.eq(ExpectedOptionsForMultiLine)
    )
  }

  test("traverse() when one param and same line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param), onSameLine = true)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      options = ArgumentMatchers.eq(ExpectedOptionsForSameLine)
    )
  }

  test("traverse() when two terms and multi-line") {
    val param1 = termParam("x")
    val param2 = termParam("y")

    termParamListTraverser.traverse(termParams = List(param1, param2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param1, param2)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      options = ArgumentMatchers.eq(ExpectedOptionsForMultiLine)
    )
  }

  test("traverse() when two terms and same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")

    termParamListTraverser.traverse(termParams = List(param1, param2), onSameLine = true)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param1, param2)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      options = ArgumentMatchers.eq(ExpectedOptionsForSameLine)
    )
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }

}
