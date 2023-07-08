package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.TryWithHandler

case class TryWithHandlerTraversalResult(override val stat: TryWithHandler) extends BlockStatTraversalResult