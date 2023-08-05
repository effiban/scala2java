package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext

trait PkgRenderContextFactory {

  def apply(enrichedPkg: EnrichedPkg): PkgRenderContext
}

private[contextfactories] class PkgRenderContextFactoryImpl(defaultStatRenderContextFactory: => DefaultStatRenderContextFactory)
  extends PkgRenderContextFactory {

  def apply(enrichedPkg: EnrichedPkg): PkgRenderContext = {
    val sealedHierarchies = enrichedPkg.sealedHierarchies
    val statRenderContextMap = enrichedPkg.enrichedStats
      .map(statResult => (statResult.stat, statResult))
      .map { case (stat, statResult) => (stat, defaultStatRenderContextFactory(statResult, sealedHierarchies)) }
      .toMap
    PkgRenderContext(statRenderContextMap)
  }
}
