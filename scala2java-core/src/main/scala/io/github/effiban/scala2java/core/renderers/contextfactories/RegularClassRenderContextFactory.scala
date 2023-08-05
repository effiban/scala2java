package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedRegularClass
import io.github.effiban.scala2java.core.renderers.contexts.RegularClassRenderContext

import scala.meta.Name

trait RegularClassRenderContextFactory {

  def apply(enrichedRegularClass: EnrichedRegularClass, permittedSubTypeNames: List[Name] = Nil): RegularClassRenderContext
}

private[contextfactories] class RegularClassRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends RegularClassRenderContextFactory {

  def apply(enrichedRegularClass: EnrichedRegularClass, permittedSubTypeNames: List[Name] = Nil): RegularClassRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedRegularClass.enrichedTemplate)
    RegularClassRenderContext(
      javaModifiers = enrichedRegularClass.javaModifiers,
      javaTypeKeyword = enrichedRegularClass.javaTypeKeyword,
      maybeInheritanceKeyword = enrichedRegularClass.maybeInheritanceKeyword,
      permittedSubTypeNames = permittedSubTypeNames,
      bodyContext = templateBodyContext
    )
  }
}

