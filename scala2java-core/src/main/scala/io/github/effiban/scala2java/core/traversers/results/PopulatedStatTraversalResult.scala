package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat;

@deprecated
trait PopulatedStatTraversalResult extends StatTraversalResult {
    val tree: Stat
}
