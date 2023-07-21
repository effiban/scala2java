package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Stat;

trait PopulatedStatTraversalResult extends StatTraversalResult {
    val tree: Stat
}
