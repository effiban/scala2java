package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCaseClass
import io.github.effiban.scala2java.core.renderers.contexts.CaseClassRenderContext

trait CaseClassRenderContextFactory {

  def apply(enrichedCaseClass: EnrichedCaseClass): CaseClassRenderContext
}

private[contextfactories] class CaseClassRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends CaseClassRenderContextFactory {

  def apply(enrichedCaseClass: EnrichedCaseClass): CaseClassRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedCaseClass.enrichedTemplate)
    CaseClassRenderContext(
      javaModifiers = enrichedCaseClass.javaModifiers,
      maybeInheritanceKeyword = enrichedCaseClass.maybeInheritanceKeyword,
      bodyContext = templateBodyContext
    )
  }
}
