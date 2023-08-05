package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, DefnRenderContext, UnsupportedDefnRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.traversers.results._

trait DefnRenderContextFactory {

  @deprecated
  def apply(DefnTraversalResult: DefnTraversalResult, sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext

  def apply(enrichedDefn: EnrichedDefn, sealedHierarchies: SealedHierarchies): DefnRenderContext
}

private[contextfactories] class DefnRenderContextFactoryImpl(traitRenderContextFactory: => TraitRenderContextFactory,
                                                             caseClassRenderContextFactory: => CaseClassRenderContextFactory,
                                                             regularClassRenderContextFactory: => RegularClassRenderContextFactory,
                                                             objectRenderContextFactory: => ObjectRenderContextFactory)
  extends DefnRenderContextFactory {

  override def apply(defnTraversalResult: DefnTraversalResult,
                     sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext = defnTraversalResult match {
    case defnVarTraversalResult: DefnVarTraversalResult => VarRenderContext(defnVarTraversalResult.javaModifiers)
    case defnDefTraversalResult: DefnDefTraversalResult => DefRenderContext(defnDefTraversalResult.javaModifiers)
    case traitTraversalResult: TraitTraversalResult => createTraitContext(traitTraversalResult, sealedHierarchies)
    case caseClassTraversalResult: CaseClassTraversalResult => caseClassRenderContextFactory(caseClassTraversalResult)
    case regularClassTraversalResult: RegularClassTraversalResult =>
      createRegularClassContext(regularClassTraversalResult, sealedHierarchies)
    case objectTraversalResult: ObjectTraversalResult => objectRenderContextFactory(objectTraversalResult)
    case _ => UnsupportedDefnRenderContext
  }

  override def apply(enrichedDefn: EnrichedDefn,
                     sealedHierarchies: SealedHierarchies): DefnRenderContext = enrichedDefn match {
    case enrichedDefnVar: EnrichedDefnVar => VarRenderContext(enrichedDefnVar.javaModifiers)
    case enrichedDefnDef: EnrichedDefnDef => DefRenderContext(enrichedDefnDef.javaModifiers)
    case enrichedTrait: EnrichedTrait => createTraitContext(enrichedTrait, sealedHierarchies)
    case enrichedCaseClass: EnrichedCaseClass => caseClassRenderContextFactory(enrichedCaseClass)
    case enrichedRegularClass: EnrichedRegularClass => UnsupportedDefnRenderContext // TODO
    case enrichedObject: EnrichedObject => UnsupportedDefnRenderContext // TODO
    case _ => UnsupportedDefnRenderContext
  }

  @deprecated
  private def createRegularClassContext(regularClassTraversalResult: RegularClassTraversalResult, sealedHierarchies: SealedHierarchies) = {
    val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(regularClassTraversalResult.name)
    regularClassRenderContextFactory(regularClassTraversalResult, permittedSubTypeNames)
  }

  @deprecated
  private def createTraitContext(traitTraversalResult: TraitTraversalResult, sealedHierarchies: SealedHierarchies) = {
    val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(traitTraversalResult.name)
    traitRenderContextFactory(traitTraversalResult, permittedSubTypeNames)
  }

  private def createTraitContext(enrichedTrait: EnrichedTrait, sealedHierarchies: SealedHierarchies) = {
    val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(enrichedTrait.name)
    traitRenderContextFactory(enrichedTrait, permittedSubTypeNames)
  }
}
