package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTrait
import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext
import io.github.effiban.scala2java.core.traversers.results.TraitTraversalResult

import scala.meta.Name

trait TraitRenderContextFactory {

  @deprecated
  def apply(traitTraversalResult: TraitTraversalResult, permittedSubTypeNames: List[Name] = Nil): TraitRenderContext

  def apply(enrichedTrait: EnrichedTrait, permittedSubTypeNames: List[Name]): TraitRenderContext
}

private[contextfactories] class TraitRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends TraitRenderContextFactory {

  def apply(traitTraversalResult: TraitTraversalResult, permittedSubTypeNames: List[Name] = Nil): TraitRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(traitTraversalResult.templateResult)
    TraitRenderContext(
      javaModifiers = traitTraversalResult.javaModifiers,
      permittedSubTypeNames = permittedSubTypeNames,
      bodyContext = templateBodyContext
    )
  }

  def apply(enrichedTrait: EnrichedTrait, permittedSubTypeNames: List[Name]): TraitRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedTrait.enrichedTemplate)
    TraitRenderContext(
      javaModifiers = enrichedTrait.javaModifiers,
      permittedSubTypeNames = permittedSubTypeNames,
      bodyContext = templateBodyContext
    )
  }
}
