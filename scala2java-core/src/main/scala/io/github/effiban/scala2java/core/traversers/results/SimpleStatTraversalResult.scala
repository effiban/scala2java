package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat;

case class SimpleStatTraversalResult(override val tree: Stat) extends PopulatedStatTraversalResult