package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult

trait CtorSecondaryRenderContextFactory {

  @deprecated
  def apply(traversalResult: CtorSecondaryTraversalResult): CtorSecondaryRenderContext

  def apply(enrichedCtorSecondary: EnrichedCtorSecondary): CtorSecondaryRenderContext
}

object CtorSecondaryRenderContextFactory extends CtorSecondaryRenderContextFactory {

  def apply(traversalResult: CtorSecondaryTraversalResult): CtorSecondaryRenderContext = {
    CtorSecondaryRenderContext(traversalResult.className, traversalResult.javaModifiers)
  }

  def apply(enrichedCtorSecondary: EnrichedCtorSecondary): CtorSecondaryRenderContext = {
    CtorSecondaryRenderContext(enrichedCtorSecondary.className, enrichedCtorSecondary.javaModifiers)
  }
}
