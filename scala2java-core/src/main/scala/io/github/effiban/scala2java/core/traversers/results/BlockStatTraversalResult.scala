package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat

case class BlockStatTraversalResult(stat: Stat, uncertainReturn: Boolean = false)
