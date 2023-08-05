package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext

trait CtorSecondaryRenderContextFactory {

  def apply(enrichedCtorSecondary: EnrichedCtorSecondary): CtorSecondaryRenderContext
}

object CtorSecondaryRenderContextFactory extends CtorSecondaryRenderContextFactory {

  def apply(enrichedCtorSecondary: EnrichedCtorSecondary): CtorSecondaryRenderContext = {
    CtorSecondaryRenderContext(enrichedCtorSecondary.className, enrichedCtorSecondary.javaModifiers)
  }
}
