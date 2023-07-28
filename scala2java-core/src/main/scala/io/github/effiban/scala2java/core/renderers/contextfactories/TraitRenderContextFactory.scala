package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext
import io.github.effiban.scala2java.core.traversers.results.TraitTraversalResult

import scala.meta.Name

trait TraitRenderContextFactory {
  def apply(traitTraversalResult: TraitTraversalResult, permittedSubTypeNames: List[Name] = Nil): TraitRenderContext
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
}
