package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.ApplyUnary
import scala.meta.XtensionQuasiquoteTerm

class ApplyUnaryTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val applyUnaryTraverser = new ApplyUnaryTraverserImpl(expressionTermTraverser)

  test("traverse") {
    val op = q"!"
    val arg = q"myFlag"
    val applyUnary = ApplyUnary(op, arg)
    val traversedArg = q"myTraversedFlag"
    val traversedApplyUnary = ApplyUnary(op, traversedArg)

    doReturn(traversedArg).when(expressionTermTraverser).traverse(eqTree(arg))

    applyUnaryTraverser.traverse(applyUnary).structure shouldBe traversedApplyUnary.structure
  }
}
