package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class FinallyTraverserImplTest extends UnitTestSuite {

  private val TheTermApply = q"log(error)"
  private val TheBlock =
    q"""
    {
      log(error)
    }
    """
  private val TheTraversedBlock =
    q"""
    {
      log(error2)
    }
    """

  private val blockWrappingTermTraverser = mock[BlockWrappingTermTraverser]

  private val finallyTraverser = new FinallyTraverserImpl(blockWrappingTermTraverser)

  test("traverse() when body is a single non-block term") {
    doReturn(BlockTraversalResult(TheTraversedBlock))
      .when(blockWrappingTermTraverser).traverse(eqTree(TheTermApply), eqBlockContext(BlockContext()))

    finallyTraverser.traverse(TheTermApply).structure shouldBe TheTraversedBlock.structure
  }

  test("traverse() when body is a block") {
    doReturn(BlockTraversalResult(TheTraversedBlock))
      .when(blockWrappingTermTraverser).traverse(eqTree(TheBlock), eqBlockContext(BlockContext()))

    finallyTraverser.traverse(TheBlock).structure shouldBe TheTraversedBlock.structure
  }
}
