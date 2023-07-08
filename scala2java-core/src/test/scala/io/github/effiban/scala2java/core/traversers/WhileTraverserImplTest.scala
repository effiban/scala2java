package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.While
import scala.meta.XtensionQuasiquoteTerm

class WhileTraverserImplTest extends UnitTestSuite {

  private val TheExpr = q"x < 3"
  private val TheTraversedExpr = q"xx < 33"

  private val TheSimpleBody = q"doIt()"
  private val TheBlockBody =
    q"""
    {
      doIt(x)
    }
    """
  private val TheTraversedBlockBody =
    q"""
    {
      doooItttt(xx)
    }
    """

  private val expressionTermTraverser = mock[ExpressionTermTraverser]
  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]

  private val whileTraverser = new WhileTraverserImpl(expressionTermTraverser, blockWrappingTermTraverser)


  test("traverse when body is a simple term") {
    val `while` = While(TheExpr, TheSimpleBody)
    val traversedWhile = While(TheTraversedExpr, TheTraversedBlockBody)

    doReturn(TheTraversedExpr).when(expressionTermTraverser).traverse(eqTree(TheExpr))
    doReturn(BlockTraversalResult(TheTraversedBlockBody))
      .when(blockWrappingTermTraverser).traverse(eqTree(TheSimpleBody), eqBlockContext(BlockContext()))

    whileTraverser.traverse(`while`).structure shouldBe traversedWhile.structure
  }

  test("traverse when body is a block") {
    val `while` = While(TheExpr, TheBlockBody)
    val traversedWhile = While(TheTraversedExpr, TheTraversedBlockBody)

    doReturn(TheTraversedExpr).when(expressionTermTraverser).traverse(eqTree(TheExpr))
    doReturn(BlockTraversalResult(TheTraversedBlockBody))
      .when(blockWrappingTermTraverser).traverse(eqTree(TheBlockBody), eqBlockContext(BlockContext()))

    whileTraverser.traverse(`while`).structure shouldBe traversedWhile.structure
  }
}
