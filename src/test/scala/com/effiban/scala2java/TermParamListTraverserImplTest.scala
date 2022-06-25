package com.effiban.scala2java

import com.effiban.scala2java.matchers.TreeListMatcher.eqTreeList
import org.mockito.ArgumentMatchers

import scala.meta.{Name, Term}

class TermParamListTraverserImplTest extends UnitTestSuite {

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termParamTraverser = mock[TermParamTraverser]

  private val termParamListTraverser = new TermParamListTraverserImpl(argumentListTraverser, termParamTraverser)


  test("traverse() when no params") {
    termParamListTraverser.traverse(Nil)

    verify(argumentListTraverser).traverse(
      args = ArgumentMatchers.eq(Nil),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }

  test("traverse() when one param and not same line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }

  test("traverse() when one param and on same line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param), onSameLine = true)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }

  test("traverse() when two terms and not on same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")

    termParamListTraverser.traverse(termParams = List(param1, param2))

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param1, param2)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      onSameLine = ArgumentMatchers.eq(false),
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }

  test("traverse() when two terms and on same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")

    termParamListTraverser.traverse(termParams = List(param1, param2), onSameLine = true)

    verify(argumentListTraverser).traverse(
      args = eqTreeList(List(param1, param2)),
      argTraverser = ArgumentMatchers.eq(termParamTraverser),
      onSameLine = ArgumentMatchers.eq(true),
      maybeDelimiterType = ArgumentMatchers.eq(Some(Parentheses))
    )
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }

}
