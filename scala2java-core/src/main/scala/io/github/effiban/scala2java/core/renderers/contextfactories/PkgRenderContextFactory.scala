package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.PkgRenderContext
import io.github.effiban.scala2java.core.traversers.results.PkgTraversalResult

trait PkgRenderContextFactory {
  def apply(traversalResult: PkgTraversalResult): PkgRenderContext
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
}
