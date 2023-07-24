package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.RegularClassRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Name

class RegularClassRenderContextMockitoMatcher(expectedContext: RegularClassRenderContext) extends ArgumentMatcher[RegularClassRenderContext] {

  override def matches(actualContext: RegularClassRenderContext): Boolean = {
    javaModifiersMatch(actualContext) &&
      javaTypeKeywordMatches(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      permittedSubTypeNamesMatch(actualContext) &&
      bodyContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaTypeKeywordMatches(actualContext: RegularClassRenderContext): Boolean = {
    actualContext.javaTypeKeyword == expectedContext.javaTypeKeyword
  }

  private def javaModifiersMatch(actualContext: RegularClassRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: RegularClassRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def permittedSubTypeNamesMatch(actualContext: RegularClassRenderContext): Boolean = {
    new ListMatcher[Name](expectedContext.permittedSubTypeNames, new TreeMatcher[Name](_)).matches(actualContext.permittedSubTypeNames)
  }

  private def bodyContextsMatch(actualContext: RegularClassRenderContext): Boolean = {
    new TemplateBodyRenderContextMockitoMatcher(expectedContext.bodyContext).matches(actualContext.bodyContext)
  }

}

object RegularClassRenderContextMockitoMatcher {
  def eqRegularClassRenderContext(expectedContext: RegularClassRenderContext): RegularClassRenderContext =
    argThat(new RegularClassRenderContextMockitoMatcher(expectedContext))
}

