package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTemplate
import io.github.effiban.scala2java.core.renderers.contexts.TemplateBodyRenderContext

trait TemplateBodyRenderContextFactory {

  def apply(enrichedTemplate: EnrichedTemplate): TemplateBodyRenderContext
}

private[contextfactories] class TemplateBodyRenderContextFactoryImpl(templateStatRenderContextFactory: => TemplateStatRenderContextFactory)
  extends TemplateBodyRenderContextFactory {

  override def apply(enrichedTemplate: EnrichedTemplate): TemplateBodyRenderContext = {
    val statRenderContextMap = enrichedTemplate.enrichedStats
      .map(statResult => (statResult.stat, statResult))
      .map { case (stat, statResult) => (stat, templateStatRenderContextFactory(statResult)) }
      .toMap
    TemplateBodyRenderContext(statRenderContextMap)
  }
}
