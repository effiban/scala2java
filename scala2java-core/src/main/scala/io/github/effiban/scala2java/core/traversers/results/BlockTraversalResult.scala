package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat
import scala.meta.Term.Block

case class BlockTraversalResult(nonLastStats: List[Stat] = Nil,
                                maybeLastStatResult: Option[BlockStatTraversalResult] = None) {

  val block: Block = Block(nonLastStats ++ maybeLastStatResult.map(_.stat))
}
