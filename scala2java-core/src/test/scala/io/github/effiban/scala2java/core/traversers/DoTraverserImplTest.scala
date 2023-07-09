package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.Term.Do
import scala.meta.XtensionQuasiquoteTerm

class DoTraverserImplTest extends UnitTestSuite {

  private val TheSimpleBody = q"action(x)"
  private val TheBlockBody =
    q"""
    {
      action(x)
    }
    """
  private val TheTraversedBlockBody =
    q"""
    {
      actionn(xx)
    }
    """
  
  private val TheExpr = q"x < 3"
  private val TheTraversedExpr = q"xx < 33"


  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]
  private val expressionTermTraverser = mock[ExpressionTermTraverser]

  private val doTraverser = new DoTraverserImpl(blockWrappingTermTraverser, expressionTermTraverser)


  test("traverse when body is a simple term") {
    val `do` = Do(TheSimpleBody, TheExpr)
    val traversedDo = Do(TheTraversedBlockBody, TheTraversedExpr)

    doReturn(TheTraversedBlockBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TheSimpleBody), eqBlockContext(BlockContext()))
    doReturn(TheTraversedExpr).when(expressionTermTraverser).traverse(eqTree(TheExpr))

    doTraverser.traverse(`do`).structure shouldBe traversedDo.structure
  }

  test("traverse when body is a block") {
    val `do` = Do(TheBlockBody, TheExpr)
    val traversedDo = Do(TheTraversedBlockBody, TheTraversedExpr)

    doReturn(TheTraversedBlockBody)
      .when(blockWrappingTermTraverser).traverse(eqTree(TheBlockBody), eqBlockContext(BlockContext()))
    doReturn(TheTraversedExpr).when(expressionTermTraverser).traverse(eqTree(TheExpr))

    doTraverser.traverse(`do`).structure shouldBe traversedDo.structure
  }
}
