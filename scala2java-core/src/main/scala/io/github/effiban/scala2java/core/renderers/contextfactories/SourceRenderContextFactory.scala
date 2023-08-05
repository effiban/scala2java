package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.SourceRenderContext
import io.github.effiban.scala2java.core.traversers.results.SourceTraversalResult

trait SourceRenderContextFactory {
  def apply(traversalResult: SourceTraversalResult): SourceRenderContext
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
}