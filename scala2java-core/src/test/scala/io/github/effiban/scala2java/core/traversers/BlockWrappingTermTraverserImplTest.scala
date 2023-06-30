package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.Yes
import io.github.effiban.scala2java.core.matchers.BlockContextMatcher.eqBlockContext
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultScalatestMatcher.equalBlockTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree

import scala.meta.XtensionQuasiquoteTerm

class BlockWrappingTermTraverserImplTest extends UnitTestSuite {

  private val defaultBlockTraverser = mock[DefaultBlockTraverser]

  private val blockWrappingTermTraverser = new BlockWrappingTermTraverserImpl(defaultBlockTraverser)

  test("traverse for a Block with shouldReturnValue = No") {
    val block =
      q"""
      {
        stat1
        stat2
      }
      """

    val traversedBlock =
      q"""
      {
        stat1
        stat2
      }
      """

    val expectedResult = TestableBlockTraversalResult(traversedBlock)

    doReturn(expectedResult).when(defaultBlockTraverser).traverse(eqTree(block), eqBlockContext(BlockContext()))

    blockWrappingTermTraverser.traverse(block) should equalBlockTraversalResult(expectedResult)
  }

  test("traverse for a Block with shouldReturnValue = Yes") {
    val block =
      q"""
      {
        stat1
        stat2
      }
      """

    val traversedBlock =
      q"""
      {
        stat1
        return stat2
      }
      """

    val context = BlockContext(shouldReturnValue = Yes)

    val expectedResult = TestableBlockTraversalResult(traversedBlock)

    doReturn(expectedResult).when(defaultBlockTraverser).traverse(eqTree(block), eqBlockContext(context))

    blockWrappingTermTraverser.traverse(block, context) should equalBlockTraversalResult(expectedResult)
  }

  test("traverse for a Term.Name with shouldReturnValue = No") {
    val term = q"x"

    val block =
      q"""
      {
        x
      }
      """

    val traversedBlock =
      q"""
      {
        y
      }
      """

    val expectedResult = TestableBlockTraversalResult(traversedBlock)

    doReturn(expectedResult).when(defaultBlockTraverser).traverse(eqTree(block), eqBlockContext(BlockContext()))

    blockWrappingTermTraverser.traverse(term) should equalBlockTraversalResult(expectedResult)
  }

  test("traverse for a Term.Name with shouldReturnValue = Yes") {
    val term = q"x"

    val block =
      q"""
      {
        x
      }
      """

    val traversedBlock =
      q"""
      {
        return y
      }
      """

    val context = BlockContext(shouldReturnValue = Yes)

    val expectedResult = TestableBlockTraversalResult(traversedBlock)

    doReturn(expectedResult).when(defaultBlockTraverser).traverse(eqTree(block), eqBlockContext(context))

    blockWrappingTermTraverser.traverse(term, context) should equalBlockTraversalResult(expectedResult)
  }
}
