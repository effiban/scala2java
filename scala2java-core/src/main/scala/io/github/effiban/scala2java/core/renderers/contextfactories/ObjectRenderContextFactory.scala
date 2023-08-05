package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedObject
import io.github.effiban.scala2java.core.renderers.contexts.ObjectRenderContext
import io.github.effiban.scala2java.core.traversers.results.ObjectTraversalResult

trait ObjectRenderContextFactory {

  @deprecated
  def apply(ObjectTraversalResult: ObjectTraversalResult): ObjectRenderContext

  def apply(enrichedObject: EnrichedObject): ObjectRenderContext
}

private[contextfactories] class ObjectRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends ObjectRenderContextFactory {

  def apply(objectTraversalResult: ObjectTraversalResult): ObjectRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(objectTraversalResult.templateResult)
    ObjectRenderContext(
      javaModifiers = objectTraversalResult.javaModifiers,
      javaTypeKeyword = objectTraversalResult.javaTypeKeyword,
      maybeInheritanceKeyword = objectTraversalResult.maybeInheritanceKeyword,
      bodyContext = templateBodyContext
    )
  }

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
