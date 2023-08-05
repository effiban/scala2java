package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedSource
import io.github.effiban.scala2java.core.renderers.contexts.SourceRenderContext

trait SourceRenderContextFactory {

  def apply(enrichedSource: EnrichedSource): SourceRenderContext
}

private[contextfactories] class SourceRenderContextFactoryImpl(defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends SourceRenderContextFactory {

  def apply(enrichedSource: EnrichedSource): SourceRenderContext = {
    val statRenderContextMap = enrichedSource.enrichedStats
      .map(statResult => (statResult.stat, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult)) }
      .toMap
    SourceRenderContext(statRenderContextMap)
  }
}
