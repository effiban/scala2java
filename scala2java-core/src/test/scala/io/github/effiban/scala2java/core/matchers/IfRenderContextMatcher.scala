package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.IfRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class IfRenderContextMatcher(expectedContext: IfRenderContext) extends ArgumentMatcher[IfRenderContext] {

  override def matches(actualContext: IfRenderContext): Boolean = {
    thenContextMatches(actualContext) && elseContextMatches(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def thenContextMatches(actualContext: IfRenderContext) = {
    new BlockRenderContextMatcher(expectedContext.thenContext).matches(actualContext.thenContext)
  }

  private def elseContextMatches(actualContext: IfRenderContext) = {
    new BlockRenderContextMatcher(expectedContext.elseContext).matches(actualContext.elseContext)
  }

}

object IfRenderContextMatcher {
  def eqIfRenderContext(expectedContext: IfRenderContext): IfRenderContext =
    argThat(new IfRenderContextMatcher(expectedContext))
}
