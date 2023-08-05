package io.github.effiban.scala2java.core.enrichers.entities

import scala.meta.Source

case class EnrichedSource(enrichedStats: List[EnrichedStat] = Nil) {
  val source: Source = Source(enrichedStats.map(_.stat))
}
