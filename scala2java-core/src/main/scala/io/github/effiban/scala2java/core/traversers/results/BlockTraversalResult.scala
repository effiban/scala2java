package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.Block
import scala.meta.{Init, Stat}

case class BlockTraversalResult(nonLastStats: List[Stat] = Nil,
                                maybeLastStatResult: Option[BlockStatTraversalResult] = None,
                                maybeInit: Option[Init] = None) {

  val block: Block = Block(nonLastStats ++ maybeLastStatResult.map(_.stat))
}
