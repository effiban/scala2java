package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DefnRenderContext, UnsupportedDefnRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{DefnTraversalResult, TraitTraversalResult}

trait DefnRenderContextFactory {
  def apply(DefnTraversalResult: DefnTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext
}

private[contextfactories] class DefnRenderContextFactoryImpl(traitRenderContextFactory: TraitRenderContextFactory) extends DefnRenderContextFactory {

  override def apply(defnTraversalResult: DefnTraversalResult,
                     sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext = defnTraversalResult match {
    case traitTraversalResult: TraitTraversalResult =>
      val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(traitTraversalResult.name)
      traitRenderContextFactory(traitTraversalResult, permittedSubTypeNames)
    case _ => UnsupportedDefnRenderContext // TODO
  }
}
