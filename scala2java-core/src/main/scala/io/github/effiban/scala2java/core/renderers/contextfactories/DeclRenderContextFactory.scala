package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, DefRenderContext, UnsupportedDeclRenderContext, VarRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclTraversalResult, DeclVarTraversalResult}

trait DeclRenderContextFactory {
  def apply(declTraversalResult: DeclTraversalResult): DeclRenderContext
}

private[contextfactories] object DeclRenderContextFactory extends DeclRenderContextFactory {

  override def apply(declTraversalResult: DeclTraversalResult): DeclRenderContext = declTraversalResult match {
    case declVarTraversalResult: DeclVarTraversalResult => VarRenderContext(declVarTraversalResult.javaModifiers)
    case declDefTraversalResult: DeclDefTraversalResult => DefRenderContext(declDefTraversalResult.javaModifiers)
    case _ => UnsupportedDeclRenderContext
  }
}
