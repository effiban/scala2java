package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedDeclDef, EnrichedDeclVar}
import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, UnsupportedDeclRenderContext, VarRenderContext}

trait DeclRenderContextFactory {

  def apply(enrichedDecl: EnrichedDecl): DeclRenderContext
}

private[contextfactories] object DeclRenderContextFactory extends DeclRenderContextFactory {

  override def apply(enrichedDecl: EnrichedDecl): DeclRenderContext = enrichedDecl match {
    case enrichedDeclVar: EnrichedDeclVar => VarRenderContext(enrichedDeclVar.javaModifiers)
    case enrichedDecDef: EnrichedDeclDef => DefRenderContext(enrichedDecDef.javaModifiers)
    case _ => UnsupportedDeclRenderContext
  }
}
