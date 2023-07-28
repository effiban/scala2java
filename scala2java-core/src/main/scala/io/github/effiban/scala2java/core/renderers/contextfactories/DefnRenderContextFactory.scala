package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DefnRenderContext, UnsupportedDefnRenderContext}
import io.github.effiban.scala2java.core.traversers.results.DefnTraversalResult

trait DefnRenderContextFactory {
  def apply(DefnTraversalResult: DefnTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext
}

private[contextfactories] class DefnRenderContextFactoryImpl extends DefnRenderContextFactory {

  override def apply(defnTraversalResult: DefnTraversalResult,
                     sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext = defnTraversalResult match {
    case _ => UnsupportedDefnRenderContext // TODO
  }
}
