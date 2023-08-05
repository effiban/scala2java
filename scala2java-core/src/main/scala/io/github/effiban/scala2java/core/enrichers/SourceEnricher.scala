package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedSource

import scala.meta.Source

trait SourceEnricher {
  def enrich(source: Source): EnrichedSource
}

private[enrichers] class SourceEnricherImpl(defaultStatEnricher: => DefaultStatEnricher) extends SourceEnricher {

  // source file
  def enrich(source: Source): EnrichedSource = {
    val enrichedStats = source.stats.map(defaultStatEnricher.enrich(_))
    EnrichedSource(enrichedStats)
  }
}
