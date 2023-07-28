package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult

trait CtorSecondaryRenderContextFactory {
  def apply(traversalResult: CtorSecondaryTraversalResult): CtorSecondaryRenderContext
}

object CtorSecondaryRenderContextFactory extends CtorSecondaryRenderContextFactory {

  def apply(traversalResult: CtorSecondaryTraversalResult): CtorSecondaryRenderContext = {
    CtorSecondaryRenderContext(traversalResult.className, traversalResult.javaModifiers)
  }
}
