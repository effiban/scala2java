package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.entities.Decision.Decision

import scala.meta.Stat
import scala.meta.Term.Block

trait DefaultBlockTraverser {

  def traverse(block: Block, context: BlockContext = BlockContext()): Block
}

private[traversers] class DefaultBlockTraverserImpl(blockStatTraverser: => BlockStatTraverser,
                                                    blockLastStatTraverser: => BlockLastStatTraverser) extends DefaultBlockTraverser {

  override def traverse(block: Block, context: BlockContext = BlockContext()): Block = {
    import context._

    block.stats match {
      case nonLastStats :+ lastStat => traverseNonEmptyStats(nonLastStats, lastStat, shouldReturnValue)
      case Nil => Block(Nil)
    }
  }

  private def traverseNonEmptyStats(nonLastStats: List[Stat], lastStat: Stat, shouldReturnValue: Decision) = {
    val traversedNonLastStats = nonLastStats.map(blockStatTraverser.traverse)
    val traversedLastStat = blockLastStatTraverser.traverse(lastStat, shouldReturnValue)
    Block(traversedNonLastStats :+ traversedLastStat)
  }
}
