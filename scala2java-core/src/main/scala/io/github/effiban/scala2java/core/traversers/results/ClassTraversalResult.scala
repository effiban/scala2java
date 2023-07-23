package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Defn

trait ClassTraversalResult extends DefnTraversalResult {
  override val tree: Defn.Class
}
