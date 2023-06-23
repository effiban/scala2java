package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class ExpressionTermTraverserImplTest extends UnitTestSuite {

  private val expressionTermRefTraverser = mock[ExpressionTermRefTraverser]
  private val expressionBlockTraverser = mock[ExpressionBlockTraverser]
  private val expressionIfTraverser = mock[ExpressionIfTraverser]
  private val defaultTermTraverser = mock[DefaultTermTraverser]

  private val expressionTraverser = new ExpressionTermTraverserImpl(
    expressionTermRefTraverser,
    expressionBlockTraverser,
    expressionIfTraverser,
    defaultTermTraverser
  )

  test("traverse() for Term.Name") {
    val termName = q"x"
    val traversedTermName = q"y"
    doReturn(traversedTermName).when(expressionTermRefTraverser).traverse(eqTree(termName))

    expressionTraverser.traverse(termName).structure shouldBe traversedTermName.structure
  }

  test("traverse() for a Block") {
    val block =
      q"""
      {
        stat1
      }
      """

    val traversedTerm = q"stat1"

    doReturn(traversedTerm).when(expressionBlockTraverser).traverse(eqTree(block))

    expressionTraverser.traverse(block).structure shouldBe traversedTerm.structure
  }

  test("traverse() for a Term.If") {
    val `if` = q"if (x < 3) calc(x) else otherCalc(x)"
    val traversedIf = q"if (xx < 33) calc(xx) else otherCalc(xx)"

    doReturn(traversedIf).when(expressionIfTraverser).traverse(eqTree(`if`))

    expressionTraverser.traverse(`if`).structure shouldBe traversedIf.structure
  }

  test("traverse() for Lit.Int") {
    val expression = q"3"
    doReturn(expression).when(defaultTermTraverser).traverse(eqTree(expression))

    expressionTraverser.traverse(expression).structure shouldBe expression.structure
  }
}
