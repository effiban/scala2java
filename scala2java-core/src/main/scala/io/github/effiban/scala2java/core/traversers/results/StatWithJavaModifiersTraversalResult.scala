package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat

trait StatWithJavaModifiersTraversalResult extends WithJavaModifiersTraversalResult {
  override val tree: Stat
}
