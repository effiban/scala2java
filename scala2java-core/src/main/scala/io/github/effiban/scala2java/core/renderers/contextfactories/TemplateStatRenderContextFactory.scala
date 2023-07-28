package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.{EmptyStatRenderContext, TemplateStatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.{CtorSecondaryTraversalResult, StatTraversalResult}

trait TemplateStatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult): TemplateStatRenderContext
}

private[contextfactories] class TemplateStatRenderContextFactoryImpl(ctorSecondaryRenderContextFactory: CtorSecondaryRenderContextFactory)
  extends TemplateStatRenderContextFactory {

  override def apply(statTraversalResult: StatTraversalResult): TemplateStatRenderContext = statTraversalResult match {
    case ctorSecondaryTraversalResult: CtorSecondaryTraversalResult => ctorSecondaryRenderContextFactory(ctorSecondaryTraversalResult)
    case _ => EmptyStatRenderContext // TODO
  }
}
