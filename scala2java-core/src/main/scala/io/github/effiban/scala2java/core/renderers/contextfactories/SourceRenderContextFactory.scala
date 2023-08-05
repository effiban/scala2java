package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedSource
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.SourceRenderContext
import io.github.effiban.scala2java.core.traversers.results.SourceTraversalResult

trait SourceRenderContextFactory {

  @deprecated
  def apply(traversalResult: SourceTraversalResult): SourceRenderContext

  def apply(enrichedSource: EnrichedSource): SourceRenderContext
}

private[contextfactories] class SourceRenderContextFactoryImpl(defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends SourceRenderContextFactory {

  def apply(traversalResult: SourceTraversalResult): SourceRenderContext = {
    val statRenderContextMap = traversalResult.statResults
      .map(statResult => (statResult.tree, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult)) }
      .toMap
    SourceRenderContext(statRenderContextMap)
  }

  def apply(enrichedSource: EnrichedSource): SourceRenderContext = {
    val statRenderContextMap = enrichedSource.enrichedStats
      .map(statResult => (statResult.stat, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult, SealedHierarchies())) }
      .toMap
    SourceRenderContext(statRenderContextMap)
  }
}
