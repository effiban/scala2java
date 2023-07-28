package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.ObjectRenderContext
import io.github.effiban.scala2java.core.traversers.results.ObjectTraversalResult

trait ObjectRenderContextFactory {
  def apply(ObjectTraversalResult: ObjectTraversalResult): ObjectRenderContext
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
}
