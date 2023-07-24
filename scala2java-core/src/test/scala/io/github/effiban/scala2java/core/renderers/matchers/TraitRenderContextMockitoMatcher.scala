package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Name

class TraitRenderContextMockitoMatcher(expectedContext: TraitRenderContext) extends ArgumentMatcher[TraitRenderContext] {

  override def matches(actualContext: TraitRenderContext): Boolean = {
    javaModifiersMatch(actualContext) &&
      permittedSubTypeNamesMatch(actualContext) &&
      bodyContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaModifiersMatch(actualContext: TraitRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def permittedSubTypeNamesMatch(actualContext: TraitRenderContext): Boolean = {
    new ListMatcher[Name](expectedContext.permittedSubTypeNames, new TreeMatcher[Name](_)).matches(actualContext.permittedSubTypeNames)
  }

  private def bodyContextsMatch(actualContext: TraitRenderContext): Boolean = {
    new TemplateBodyRenderContextMockitoMatcher(expectedContext.bodyContext).matches(actualContext.bodyContext)
  }

}

object TraitRenderContextMockitoMatcher {
  def eqTraitRenderContext(expectedContext: TraitRenderContext): TraitRenderContext =
    argThat(new TraitRenderContextMockitoMatcher(expectedContext))
}

