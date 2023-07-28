package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.TemplateStatRenderContext
import io.github.effiban.scala2java.core.traversers.results.StatTraversalResult

trait TemplateStatRenderContextFactory {
  def apply(statTraversalResult: StatTraversalResult): TemplateStatRenderContext
}

private[contextfactories] class TemplateStatRenderContextFactoryImpl extends TemplateStatRenderContextFactory {

  override def apply(statTraversalResult: StatTraversalResult): TemplateStatRenderContext =
    new TemplateStatRenderContext() {} // TODO
}
