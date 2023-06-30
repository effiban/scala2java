package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Eta
import scala.meta.XtensionQuasiquoteTerm

class EtaTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val etaTraverser = new EtaTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val methodName = q"myMethod"
    val traversedMethodName = q"myTraversedMethod"

    doReturn(traversedMethodName).when(expressionTermTraverser).traverse(eqTree(methodName))

    etaTraverser.traverse(Eta(methodName)).structure shouldBe Eta(traversedMethodName).structure
  }
}
