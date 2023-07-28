package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{DeclRenderContext, UnsupportedDeclRenderContext}
import io.github.effiban.scala2java.core.traversers.results.DeclTraversalResult

trait DeclRenderContextFactory {
  def apply(declTraversalResult: DeclTraversalResult): DeclRenderContext
}

private[contextfactories] class DeclRenderContextFactoryImpl extends DeclRenderContextFactory {

  override def apply(declTraversalResult: DeclTraversalResult): DeclRenderContext = declTraversalResult match {
    case _ => UnsupportedDeclRenderContext // TODO
  }
}
