package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Init
import scala.meta.Term.Block

case class BlockTraversalResult(block: Block,
                                maybeInit: Option[Init] = None,
                                uncertainReturn: Boolean = false)
