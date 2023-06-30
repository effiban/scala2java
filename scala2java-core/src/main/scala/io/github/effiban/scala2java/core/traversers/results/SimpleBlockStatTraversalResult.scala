package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat

case class SimpleBlockStatTraversalResult(override val stat: Stat, uncertainReturn: Boolean = false)
  extends BlockStatTraversalResult
