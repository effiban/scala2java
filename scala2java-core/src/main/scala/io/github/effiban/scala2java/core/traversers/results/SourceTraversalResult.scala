package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Source

@deprecated
case class SourceTraversalResult(statResults: List[PopulatedStatTraversalResult] = Nil) {
  val source: Source = Source(statResults.map(_.tree))
}
