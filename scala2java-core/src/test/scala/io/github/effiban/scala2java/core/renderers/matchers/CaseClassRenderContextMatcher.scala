package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.CaseClassRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class CaseClassRenderContextMatcher(expectedContext: CaseClassRenderContext) extends ArgumentMatcher[CaseClassRenderContext] {

  override def matches(actualContext: CaseClassRenderContext): Boolean = {
    javaModifiersMatch(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      bodyContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaModifiersMatch(actualContext: CaseClassRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: CaseClassRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def bodyContextsMatch(actualContext: CaseClassRenderContext): Boolean = {
    new TemplateBodyRenderContextMatcher(expectedContext.bodyContext).matches(actualContext.bodyContext)
  }

}

object CaseClassRenderContextMatcher {
  def eqCaseClassRenderContext(expectedContext: CaseClassRenderContext): CaseClassRenderContext =
    argThat(new CaseClassRenderContextMatcher(expectedContext))
}

