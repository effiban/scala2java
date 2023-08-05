package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedDeclDef, EnrichedDeclVar}
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, UnsupportedDeclRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclTraversalResult, DeclVarTraversalResult}

trait DeclRenderContextFactory {

  @deprecated
  def apply(declTraversalResult: DeclTraversalResult): DeclRenderContext

  def apply(enrichedDecl: EnrichedDecl): DeclRenderContext
}

private[contextfactories] object DeclRenderContextFactory extends DeclRenderContextFactory {

  override def apply(enrichedDecl: DeclTraversalResult): DeclRenderContext = enrichedDecl match {
    case declVarTraversalResult: DeclVarTraversalResult => VarRenderContext(declVarTraversalResult.javaModifiers)
    case declDefTraversalResult: DeclDefTraversalResult => DefRenderContext(declDefTraversalResult.javaModifiers)
    case _ => UnsupportedDeclRenderContext
  }

  override def apply(enrichedDecl: EnrichedDecl): DeclRenderContext = enrichedDecl match {
    case enrichedDeclVar: EnrichedDeclVar => VarRenderContext(enrichedDeclVar.javaModifiers)
    case enrichedDecDef: EnrichedDeclDef => DefRenderContext(enrichedDecDef.javaModifiers)
    case _ => UnsupportedDeclRenderContext
  }
}
