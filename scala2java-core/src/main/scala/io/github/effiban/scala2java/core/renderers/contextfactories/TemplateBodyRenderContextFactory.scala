package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext
import io.github.effiban.scala2java.core.traversers.results.TemplateTraversalResult

trait TemplateBodyRenderContextFactory {
  def apply(templateTraversalResult: TemplateTraversalResult): TemplateBodyRenderContext
}

private[contextfactories] class TemplateBodyRenderContextFactoryImpl(templateStatRenderContextFactory: => TemplateStatRenderContextFactory)
  extends TemplateBodyRenderContextFactory {

  override def apply(templateTraversalResult: TemplateTraversalResult): TemplateBodyRenderContext = {
    val statRenderContextMap = templateTraversalResult.statResults
      .map(statResult => (statResult.tree, statResult))
      .map { case (stat, statResult) => (stat, templateStatRenderContextFactory(statResult)) }
      .toMap
    TemplateBodyRenderContext(statRenderContextMap)
  }
}
