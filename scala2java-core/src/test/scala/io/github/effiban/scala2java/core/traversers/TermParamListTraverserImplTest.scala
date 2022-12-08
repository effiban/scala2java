package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.StatContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.entities.ListTraversalOptions
import io.github.effiban.scala2java.core.matchers.CombinedMatchers.eqTreeList
import io.github.effiban.scala2java.core.matchers.TreeMatcher.eqTree
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.spi.entities.JavaScope
import org.mockito.{ArgumentCaptor, ArgumentMatchers}

import scala.meta.{Name, Term}

class TermParamListTraverserImplTest extends UnitTestSuite {

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

  private val argumentListTraverser = mock[ArgumentListTraverser]
  private val termParamTraverser = mock[TermParamTraverser]
  private val argTraverserCaptor: ArgumentCaptor[ScalaTreeTraverser[Term.Param]] = ArgumentCaptor.forClass(classOf[ScalaTreeTraverser[Term.Param]])

  private val termParamListTraverser = new TermParamListTraverserImpl(argumentListTraverser, termParamTraverser)


  test("traverse() when no params") {
    termParamListTraverser.traverse(termParams = Nil, context = TheStatContext)

    verifyArgumentListTraverserInvocation(Nil, ExpectedOptionsForMultiLine)
  }

  test("traverse() when one param and multi-line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param), context = TheStatContext)

    verifyArgumentListTraverserInvocation(List(param), ExpectedOptionsForMultiLine)
  }

  test("traverse() when one param and same line") {
    val param = termParam("x")

    termParamListTraverser.traverse(termParams = List(param), context = TheStatContext, onSameLine = true)

    verifyArgumentListTraverserInvocation(List(param), ExpectedOptionsForSameLine)
  }

  test("traverse() when two terms and multi-line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)

    termParamListTraverser.traverse(termParams = params, context = TheStatContext)

    verifyArgumentListTraverserInvocation(params, ExpectedOptionsForMultiLine)
  }

  test("traverse() when two terms and same line") {
    val param1 = termParam("x")
    val param2 = termParam("y")
    val params = List(param1, param2)

    termParamListTraverser.traverse(termParams = params, context = TheStatContext, onSameLine = true)

    verifyArgumentListTraverserInvocation(params, ExpectedOptionsForSameLine)
  }

  private def verifyArgumentListTraverserInvocation(args: List[Term.Param], options: ListTraversalOptions): Unit = {
    verify(argumentListTraverser).traverse(
      args = eqTreeList(args),
      argTraverser = argTraverserCaptor.capture(),
      options = ArgumentMatchers.eq(options)
    )

    val argTraverser = argTraverserCaptor.getValue
    val dummyTermParam = termParam("dummy")
    argTraverser.traverse(dummyTermParam)
    verify(termParamTraverser).traverse(eqTree(dummyTermParam), ArgumentMatchers.eq(TheStatContext))
  }

  private def termParam(name: String) = {
    Term.Param(mods = Nil, name = Name.Indeterminate(name), decltpe = None, default = None)
  }
}
