package io.github.effiban.scala2java.core.enrichers

import io.github.effiban.scala2java.core.enrichers.contexts.TemplateEnrichmentContext
import io.github.effiban.scala2java.core.enrichers.entities.EnrichedMultiStat

import scala.meta.Stat

trait TemplateBodyEnricher {

  def enrich(statements: List[Stat], context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedMultiStat
}

private[enrichers] class TemplateBodyEnricherImpl(templateStatEnricher: => TemplateStatEnricher) extends TemplateBodyEnricher {

  def enrich(stats: List[Stat], context: TemplateEnrichmentContext = TemplateEnrichmentContext()): EnrichedMultiStat = {
    val enrichedStats = stats.map(stat => templateStatEnricher.enrich(stat, context))
    EnrichedMultiStat(enrichedStats)
  }
}
