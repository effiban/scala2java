package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermTraverserImplTest extends UnitTestSuite {

  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val expressionTraverser = new ExpressionTermTraverserImpl(
    defaultTermTraverser
  )


  test("traverse() for Lit.Int") {
    val expression = q"3"
    doReturn(expression).when(defaultTermTraverser).traverse(eqTree(expression))

    expressionTraverser.traverse(expression).structure shouldBe expression.structure
  }
}
