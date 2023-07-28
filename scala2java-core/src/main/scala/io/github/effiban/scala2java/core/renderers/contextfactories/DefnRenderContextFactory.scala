package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, DefnRenderContext, UnsupportedDefnRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.traversers.results._

trait DefnRenderContextFactory {
  def apply(DefnTraversalResult: DefnTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext
}

private[contextfactories] class DefnRenderContextFactoryImpl(traitRenderContextFactory: => TraitRenderContextFactory,
                                                             objectRenderContextFactory: => ObjectRenderContextFactory)
  extends DefnRenderContextFactory {

  override def apply(defnTraversalResult: DefnTraversalResult,
                     sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext = defnTraversalResult match {
    case defnVarTraversalResult: DefnVarTraversalResult => VarRenderContext(defnVarTraversalResult.javaModifiers)
    case defnDefTraversalResult: DefnDefTraversalResult => DefRenderContext(defnDefTraversalResult.javaModifiers)
    case traitTraversalResult: TraitTraversalResult =>
      val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(traitTraversalResult.name)
      traitRenderContextFactory(traitTraversalResult, permittedSubTypeNames)
    case objectTraversalResult: ObjectTraversalResult => objectRenderContextFactory(objectTraversalResult)
    case _ => UnsupportedDefnRenderContext // TODO
  }
}
