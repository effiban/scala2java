package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCaseClass
import io.github.effiban.scala2java.core.renderers.contexts.CaseClassRenderContext
import io.github.effiban.scala2java.core.traversers.results.CaseClassTraversalResult

trait CaseClassRenderContextFactory {

  @deprecated
  def apply(CaseClassTraversalResult: CaseClassTraversalResult): CaseClassRenderContext

  def apply(enrichedCaseClass: EnrichedCaseClass): CaseClassRenderContext
}

private[contextfactories] class CaseClassRenderContextFactoryImpl(templateBodyRenderContextFactory: => TemplateBodyRenderContextFactory)
  extends CaseClassRenderContextFactory {

  def apply(CaseClassTraversalResult: CaseClassTraversalResult): CaseClassRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(CaseClassTraversalResult.templateResult)
    CaseClassRenderContext(
      javaModifiers = CaseClassTraversalResult.javaModifiers,
      maybeInheritanceKeyword = CaseClassTraversalResult.maybeInheritanceKeyword,
      bodyContext = templateBodyContext
    )
  }

  def apply(enrichedCaseClass: EnrichedCaseClass): CaseClassRenderContext = {
    val templateBodyContext = templateBodyRenderContextFactory(enrichedCaseClass.enrichedTemplate)
    CaseClassRenderContext(
      javaModifiers = enrichedCaseClass.javaModifiers,
      maybeInheritanceKeyword = enrichedCaseClass.maybeInheritanceKeyword,
      bodyContext = templateBodyContext
    )
  }
}
