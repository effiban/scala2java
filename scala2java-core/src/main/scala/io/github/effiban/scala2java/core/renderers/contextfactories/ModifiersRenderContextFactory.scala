package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.ModifiersRenderContext
import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult

trait ModifiersRenderContextFactory {
  def apply(traversalResult: ModListTraversalResult, annotsOnSameLine: Boolean = false): ModifiersRenderContext
}

object ModifiersRenderContextFactory extends ModifiersRenderContextFactory {

  def apply(traversalResult: ModListTraversalResult, annotsOnSameLine: Boolean = false): ModifiersRenderContext = {
    ModifiersRenderContext(
      scalaMods = traversalResult.scalaMods,
      annotsOnSameLine = annotsOnSameLine,
      javaModifiers = traversalResult.javaModifiers
    )
  }
}
