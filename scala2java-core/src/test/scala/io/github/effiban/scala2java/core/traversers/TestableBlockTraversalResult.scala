package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.{BlockTraversalResult, SimpleBlockStatTraversalResult}

import scala.meta.Term.Block

object TestableBlockTraversalResult {

  def apply(block: Block = Block(Nil), uncertainReturn: Boolean = false): BlockTraversalResult = {
    BlockTraversalResult(
      nonLastStats = if (block.stats.isEmpty) Nil else block.stats.slice(0, block.stats.length - 1),
      maybeLastStatResult = block.stats.lastOption.map(stat => SimpleBlockStatTraversalResult(stat, uncertainReturn)),
    )
  }
}
