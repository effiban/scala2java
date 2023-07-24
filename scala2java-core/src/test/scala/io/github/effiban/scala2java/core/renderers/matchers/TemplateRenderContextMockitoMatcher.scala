package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TemplateRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Name

class TemplateRenderContextMockitoMatcher(expectedContext: TemplateRenderContext) extends ArgumentMatcher[TemplateRenderContext] {

  override def matches(actualContext: TemplateRenderContext): Boolean = {
    maybeInheritanceKeywordMatches(actualContext) &&
      permittedSubTypeNamesMatch(actualContext) &&
      bodyContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeInheritanceKeywordMatches(actualContext: TemplateRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def permittedSubTypeNamesMatch(actualContext: TemplateRenderContext): Boolean = {
    new ListMatcher[Name](expectedContext.permittedSubTypeNames, new TreeMatcher[Name](_)).matches(actualContext.permittedSubTypeNames)
  }

  private def bodyContextsMatch(actualContext: TemplateRenderContext): Boolean = {
    new TemplateBodyRenderContextMockitoMatcher(expectedContext.bodyContext).matches(actualContext.bodyContext)
  }

}

object TemplateRenderContextMockitoMatcher {
  def eqTemplateRenderContext(expectedContext: TemplateRenderContext): TemplateRenderContext =
    argThat(new TemplateRenderContextMockitoMatcher(expectedContext))
}

