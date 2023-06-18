package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Return
import scala.meta.XtensionQuasiquoteTerm

class ReturnTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val returnTraverser = new ReturnTraverserImpl(expressionTermTraverser)

  test("traverse()") {
    val x = q"x"
    val y = q"y"

    doReturn(y).when(expressionTermTraverser).traverse(eqTree(x))

    returnTraverser.traverse(Return(x)).structure shouldBe Return(y).structure
  }
}
