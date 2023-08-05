package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTrait
import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext

import scala.meta.Name

trait TraitRenderContextFactory {

  def apply(enrichedTrait: EnrichedTrait, permittedSubTypeNames: List[Name]): TraitRenderContext
}

private[contextfactories] class TraitRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends TraitRenderContextFactory {

  def apply(enrichedTrait: EnrichedTrait, permittedSubTypeNames: List[Name]): TraitRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedTrait.enrichedTemplate)
    TraitRenderContext(
      javaModifiers = enrichedTrait.javaModifiers,
      permittedSubTypeNames = permittedSubTypeNames,
      bodyContext = templateBodyContext
    )
  }
}
