package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.renderers.contexts.RegularClassRenderContext
import io.github.effiban.scala2java.core.traversers.results.RegularClassTraversalResult

import scala.meta.Name

trait RegularClassRenderContextFactory {

  def apply(regularClassTraversalResult: RegularClassTraversalResult, permittedSubTypeNames: List[Name] = Nil): RegularClassRenderContext
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
}

