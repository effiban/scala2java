package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.matchers.BlockTraversalResultScalatestMatcher.equalBlockTraversalResult
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.core.traversers.results.{BlockStatTraversalResult, BlockTraversalResult}
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.{Stat, XtensionQuasiquoteInit, XtensionQuasiquoteTerm}

class BlockTraverserImplTest extends UnitTestSuite {

  private val Stat1 = q"stat1"
  private val Stat2 = q"stat2"
  private val Stat3 = q"stat3"

  private val TraversedStat1 = q"traversedStat1"
  private val TraversedStat2 = q"traversedStat2"
  private val TraversedStat3 = q"traversedStat3"

  private val initTraverser = mock[InitTraverser]
  private val blockStatTraverser = mock[BlockStatTraverser]
  private val blockLastStatTraverser = mock[BlockLastStatTraverser]

  private val blockTraverser = new BlockTraverserImpl(
    initTraverser,
    blockStatTraverser,
    blockLastStatTraverser
  )


  test("traverse() when block is empty") {
    val emptyBlock = Block(List.empty)
    blockTraverser.traverse(emptyBlock) should equalBlockTraversalResult(BlockTraversalResult(emptyBlock))
  }

  test("traverse() for block of one statement, shouldReturnValue=No") {
    val block = Block(List(Stat1))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat1)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1)))

    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat1), eqTo(No))

    blockTraverser.traverse(block) should equalBlockTraversalResult(expectedBlockResult)
  }

  test("traverse() for block of two statements, shouldReturnValue=Yes") {
    val block = Block(List(Stat1, Stat2))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat2)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1, TraversedStat2)))

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(Yes))

    blockTraverser.traverse(block, BlockContext(shouldReturnValue = Yes)) should equalBlockTraversalResult(expectedBlockResult)
  }

  test("traverse() for block of two statements, shouldReturnValue=No") {
    val block = Block(List(Stat1, Stat2))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat2)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1, TraversedStat2)))

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(No))

    blockTraverser.traverse(block) should equalBlockTraversalResult(expectedBlockResult)
  }

  test("traverse() for block of two statements, shouldReturnValue=Uncertain") {
    val block = Block(List(Stat1, Stat2))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat2, uncertainReturn = true)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1, TraversedStat2)), uncertainReturn = true)

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(Uncertain))

    blockTraverser.traverse(block, BlockContext(shouldReturnValue = Uncertain)) should equalBlockTraversalResult(expectedBlockResult)
  }

  test("traverse() for block of three statements, shouldReturnValue=No") {
    val block = Block(List(Stat1, Stat2, Stat3))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat3)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1, TraversedStat2, TraversedStat3)))

    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == Stat1.structure => TraversedStat1
      case aStat if aStat.structure == Stat2.structure => TraversedStat2
      case aStat => aStat
    }).when(blockStatTraverser).traverse(any[Stat])
    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat3), eqTo(No))

    blockTraverser.traverse(block) should equalBlockTraversalResult(expectedBlockResult)
  }


  test("traverse() for an 'init' and one statement") {
    val init = init"this(x)"
    val traversedInit = init"this(xx)"
    val block = Block(List(Stat1))
    val expectedLastStatResult = BlockStatTraversalResult(TraversedStat1)
    val expectedBlockResult = BlockTraversalResult(block = Block(List(TraversedStat1)), maybeInit = Some(traversedInit))

    doReturn(traversedInit).when(initTraverser).traverse(eqTree(init))
    doReturn(expectedLastStatResult).when(blockLastStatTraverser).traverse(eqTree(Stat1), eqTo(No))

    blockTraverser.traverse(block, BlockContext(maybeInit = Some(init))) should equalBlockTraversalResult(expectedBlockResult)
  }
}
