package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.CaseClassRenderContext
import io.github.effiban.scala2java.core.traversers.results.CaseClassTraversalResult

trait CaseClassRenderContextFactory {
  def apply(CaseClassTraversalResult: CaseClassTraversalResult): CaseClassRenderContext
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
}
