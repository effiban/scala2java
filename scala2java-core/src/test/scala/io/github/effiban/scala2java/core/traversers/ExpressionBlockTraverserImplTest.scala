package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Block
import scala.meta.XtensionQuasiquoteTerm

class ExpressionBlockTraverserImplTest extends UnitTestSuite {

  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val expressionBlockTraverser = new ExpressionBlockTraverserImpl(expressionTermTraverser)

  test("traverse() for a Block of one term") {
    val term = q"x"
    val traversedTerm = q"y"

    doReturn(traversedTerm).when(expressionTermTraverser).traverse(eqTree(term))

    expressionBlockTraverser.traverse(Block(List(term))).structure shouldBe traversedTerm.structure
  }

  test("traverse() for a Block of two statements") {
    val block =
      q"""
      {
        stat1
        stat2
      }
      """

    val expectedTermApply =
      q"""
      (() => {
        stat1
        stat2
      }).apply()
      """

    val expectedTraversedTermApply =
      q"""
      (() => {
        traversedStat1
        traversedStat2
      }).apply()
      """

    doReturn(expectedTraversedTermApply).when(expressionTermTraverser).traverse(eqTree(expectedTermApply))

    expressionBlockTraverser.traverse(block).structure shouldBe expectedTraversedTermApply.structure
  }
}
