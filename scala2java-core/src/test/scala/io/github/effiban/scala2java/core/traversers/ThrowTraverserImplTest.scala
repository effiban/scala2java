package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Throw
import scala.meta.XtensionQuasiquoteTerm

class ThrowTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val throwTraverser = new ThrowTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val ex = q"myException"
    val traversedEx = q"myTraversedException"

    doReturn(traversedEx).when(expressionTermTraverser).traverse(eqTree(ex))

    throwTraverser.traverse(Throw(ex)).structure shouldBe Throw(traversedEx).structure
  }
}
