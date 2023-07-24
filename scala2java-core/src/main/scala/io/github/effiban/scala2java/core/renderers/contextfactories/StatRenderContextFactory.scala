package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.StatRenderContext
import io.github.effiban.scala2java.core.traversers.results.StatTraversalResult

trait StatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult): StatRenderContext
}
