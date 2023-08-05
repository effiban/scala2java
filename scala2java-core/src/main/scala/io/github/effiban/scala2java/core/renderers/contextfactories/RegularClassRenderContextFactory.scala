package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedRegularClass
import io.github.effiban.scala2java.core.renderers.contexts.RegularClassRenderContext
import io.github.effiban.scala2java.core.traversers.results.RegularClassTraversalResult

import scala.meta.Name

trait RegularClassRenderContextFactory {

  @deprecated
  def apply(regularClassTraversalResult: RegularClassTraversalResult, permittedSubTypeNames: List[Name] = Nil): RegularClassRenderContext

  def apply(enrichedRegularClass: EnrichedRegularClass, permittedSubTypeNames: List[Name]): RegularClassRenderContext
}

private[contextfactories] class RegularClassRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends RegularClassRenderContextFactory {

  def apply(regularClassTraversalResult: RegularClassTraversalResult,
            permittedSubTypeNames: List[Name] = Nil): RegularClassRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(regularClassTraversalResult.templateResult)
    RegularClassRenderContext(
      javaModifiers = regularClassTraversalResult.javaModifiers,
      javaTypeKeyword = regularClassTraversalResult.javaTypeKeyword,
      maybeInheritanceKeyword = regularClassTraversalResult.maybeInheritanceKeyword,
      permittedSubTypeNames = permittedSubTypeNames,
      bodyContext = templateBodyContext
    )
  }

  def apply(enrichedRegularClass: EnrichedRegularClass, permittedSubTypeNames: List[Name]): RegularClassRenderContext = {
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

