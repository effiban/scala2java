package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedObject
import io.github.effiban.scala2java.core.renderers.contexts.ObjectRenderContext

trait ObjectRenderContextFactory {

  def apply(enrichedObject: EnrichedObject): ObjectRenderContext
}

private[contextfactories] class ObjectRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends ObjectRenderContextFactory {

  def apply(enrichedObject: EnrichedObject): ObjectRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedObject.enrichedTemplate)
    ObjectRenderContext(
      javaModifiers = enrichedObject.javaModifiers,
      javaTypeKeyword = enrichedObject.javaTypeKeyword,
      maybeInheritanceKeyword = enrichedObject.maybeInheritanceKeyword,
      bodyContext = templateBodyContext
    )
  }
}
