package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities._
import io.github.effiban.scala2java.core.entities.SealedHierarchies
import io.github.effiban.scala2java.core.renderers.contexts.{DefRenderContext, DefnRenderContext, UnsupportedDefnRenderContext, VarRenderContext}

trait DefnRenderContextFactory {

  def apply(enrichedDefn: EnrichedDefn,
            sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext
}

private[contextfactories] class DefnRenderContextFactoryImpl(traitRenderContextFactory: => TraitRenderContextFactory,
                                                             caseClassRenderContextFactory: => CaseClassRenderContextFactory,
                                                             regularClassRenderContextFactory: => RegularClassRenderContextFactory,
                                                             objectRenderContextFactory: => ObjectRenderContextFactory)
  extends DefnRenderContextFactory {

  override def apply(enrichedDefn: EnrichedDefn,
                     sealedHierarchies: SealedHierarchies = SealedHierarchies()): DefnRenderContext = enrichedDefn match {
    case enrichedDefnVar: EnrichedDefnVar => VarRenderContext(enrichedDefnVar.javaModifiers)
    case enrichedDefnDef: EnrichedDefnDef => DefRenderContext(enrichedDefnDef.javaModifiers)
    case enrichedTrait: EnrichedTrait => createTraitContext(enrichedTrait, sealedHierarchies)
    case enrichedCaseClass: EnrichedCaseClass => caseClassRenderContextFactory(enrichedCaseClass)
    case enrichedRegularClass: EnrichedRegularClass => createRegularClassContext(enrichedRegularClass, sealedHierarchies)
    case enrichedObject: EnrichedObject => objectRenderContextFactory(enrichedObject)
    case _ => UnsupportedDefnRenderContext
  }

  private def createRegularClassContext(enrichedRegularClass: EnrichedRegularClass, sealedHierarchies: SealedHierarchies) = {
    val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(enrichedRegularClass.name)
    regularClassRenderContextFactory(enrichedRegularClass, permittedSubTypeNames)
  }

  private def createTraitContext(enrichedTrait: EnrichedTrait, sealedHierarchies: SealedHierarchies) = {
    val permittedSubTypeNames = sealedHierarchies.getSubTypeNames(enrichedTrait.name)
    traitRenderContextFactory(enrichedTrait, permittedSubTypeNames)
  }
}
