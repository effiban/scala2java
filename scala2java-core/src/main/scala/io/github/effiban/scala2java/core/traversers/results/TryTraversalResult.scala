package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term

case class TryTraversalResult(override val stat: Term.Try) extends BlockStatTraversalResult
