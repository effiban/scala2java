package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.{No, Uncertain, Yes}
import io.github.effiban.scala2java.core.testsuites.UnitTestSuite
import io.github.effiban.scala2java.test.utils.matchers.TreeMatcher.eqTree
import org.mockito.ArgumentMatchersSugar.{any, eqTo}

import scala.meta.Term.Block
import scala.meta.{Stat, XtensionQuasiquoteTerm}

class DefaultBlockTraverserImplTest extends UnitTestSuite {

  private val Stat1 = q"stat1"
  private val Stat2 = q"stat2"
  private val Stat3 = q"stat3"

  private val TraversedStat1 = q"traversedStat1"
  private val TraversedStat2 = q"traversedStat2"
  private val TraversedStat3 = q"traversedStat3"

  private val blockStatTraverser = mock[BlockStatTraverser]
  private val blockLastStatTraverser = mock[BlockLastStatTraverser]

  private val blockTraverser = new DefaultBlockTraverserImpl(
    blockStatTraverser,
    blockLastStatTraverser
  )


  test("traverse() when block is empty") {
    val emptyBlock = Block(List.empty)
    blockTraverser.traverse(emptyBlock).structure shouldBe emptyBlock.structure
  }

  test("traverse() for block of one statement, shouldReturnValue=No") {
    val block = Block(List(Stat1))
    val expectedBlockResult = Block(List(TraversedStat1))

    doReturn(TraversedStat1).when(blockLastStatTraverser).traverse(eqTree(Stat1), eqTo(No))

    blockTraverser.traverse(block).structure shouldBe expectedBlockResult.structure
  }

  test("traverse() for block of two statements, shouldReturnValue=Yes") {
    val block = Block(List(Stat1, Stat2))
    val expectedBlockResult = Block(List(TraversedStat1, TraversedStat2))

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(TraversedStat2).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(Yes))

    val actualResult = blockTraverser.traverse(block, BlockContext(shouldReturnValue = Yes))
    actualResult.structure shouldBe expectedBlockResult.structure
  }

  test("traverse() for block of two statements, shouldReturnValue=No") {
    val block = Block(List(Stat1, Stat2))
    val expectedBlockResult = Block(List(TraversedStat1, TraversedStat2))

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(TraversedStat2).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(No))

    blockTraverser.traverse(block).structure shouldBe expectedBlockResult.structure
  }

  test("traverse() for block of two statements, shouldReturnValue=Uncertain") {
    val block = Block(List(Stat1, Stat2))
    val expectedBlockResult = Block(List(TraversedStat1, TraversedStat2))

    doReturn(TraversedStat1).when(blockStatTraverser).traverse(eqTree(Stat1))
    doReturn(TraversedStat2).when(blockLastStatTraverser).traverse(eqTree(Stat2), eqTo(Uncertain))

    val actualResult = blockTraverser.traverse(block, BlockContext(shouldReturnValue = Uncertain))
    actualResult.structure shouldBe expectedBlockResult.structure
  }

  test("traverse() for block of three statements, shouldReturnValue=No") {
    val block = Block(List(Stat1, Stat2, Stat3))
    val expectedBlockResult = Block(List(TraversedStat1, TraversedStat2, TraversedStat3))

    doAnswer((stat: Stat) => stat match {
      case aStat if aStat.structure == Stat1.structure => TraversedStat1
      case aStat if aStat.structure == Stat2.structure => TraversedStat2
      case aStat => aStat
    }).when(blockStatTraverser).traverse(any[Stat])
    doReturn(TraversedStat3).when(blockLastStatTraverser).traverse(eqTree(Stat3), eqTo(No))

    blockTraverser.traverse(block).structure shouldBe expectedBlockResult.structure
  }
}
