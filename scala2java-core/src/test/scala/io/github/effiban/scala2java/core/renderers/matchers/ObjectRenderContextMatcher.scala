package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.ObjectRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ObjectRenderContextMatcher(expectedContext: ObjectRenderContext) extends ArgumentMatcher[ObjectRenderContext] {

  override def matches(actualContext: ObjectRenderContext): Boolean = {
    javaModifiersMatch(actualContext) &&
      javaTypeKeywordMatches(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      bodyContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaTypeKeywordMatches(actualContext: ObjectRenderContext): Boolean = {
    actualContext.javaTypeKeyword == expectedContext.javaTypeKeyword
  }

  private def javaModifiersMatch(actualContext: ObjectRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: ObjectRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def bodyContextsMatch(actualContext: ObjectRenderContext): Boolean = {
    new TemplateBodyRenderContextMatcher(expectedContext.bodyContext).matches(actualContext.bodyContext)
  }

}

object ObjectRenderContextMatcher {
  def eqObjectRenderContext(expectedContext: ObjectRenderContext): ObjectRenderContext =
    argThat(new ObjectRenderContextMatcher(expectedContext))
}

