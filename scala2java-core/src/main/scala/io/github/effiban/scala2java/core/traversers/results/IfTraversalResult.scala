package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.If

case class IfTraversalResult(override val stat: If) extends BlockStatTraversalResult
