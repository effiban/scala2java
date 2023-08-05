package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Defn

@deprecated
trait ClassTraversalResult extends DefnTraversalResult {
  override val tree: Defn.Class
}
