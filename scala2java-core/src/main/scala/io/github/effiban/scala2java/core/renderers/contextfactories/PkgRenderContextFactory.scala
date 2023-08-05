package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.core.traversers.results.PkgTraversalResult

trait PkgRenderContextFactory {

  @deprecated
  def apply(traversalResult: PkgTraversalResult): PkgRenderContext

  def apply(enrichedPkg: EnrichedPkg): PkgRenderContext
}

private[contextfactories] class PkgRenderContextFactoryImpl(defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends PkgRenderContextFactory {

  def apply(traversalResult: PkgTraversalResult): PkgRenderContext = {
    val sealedHierarchies = traversalResult.sealedHierarchies
    val statRenderContextMap = traversalResult.statResults
      .map(statResult => (statResult.tree, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult, sealedHierarchies)) }
      .toMap
    PkgRenderContext(statRenderContextMap)
  }

  def apply(enrichedPkg: EnrichedPkg): PkgRenderContext = {
    val sealedHierarchies = enrichedPkg.sealedHierarchies
    val statRenderContextMap = enrichedPkg.enrichedStats
      .map(statResult => (statResult.stat, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult, sealedHierarchies)) }
      .toMap
    PkgRenderContext(statRenderContextMap)
  }
}
