package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.{BlockStatRenderContext, IfRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockStatRenderContextMockitoMatcher(expectedContext: BlockStatRenderContext) extends ArgumentMatcher[BlockStatRenderContext] {

  override def matches(actualContext: BlockStatRenderContext): Boolean = {
    matchesByType(actualContext)
  }

  private def matchesByType(actualContext: BlockStatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualContext: SimpleBlockStatRenderContext, expectedContext: SimpleBlockStatRenderContext) =>
        actualContext == expectedContext
      case (actualContext: IfRenderContext, expectedContext: IfRenderContext) =>
        new IfRenderContextMockitoMatcher(expectedContext).matches(actualContext)
      case (actualContext: TryRenderContext, expectedContext: TryRenderContext) =>
        new TryRenderContextMockitoMatcher(expectedContext).matches(actualContext)
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockStatRenderContextMockitoMatcher {
  def eqBlockStatRenderContext(expectedContext: BlockStatRenderContext): BlockStatRenderContext =
    argThat(new BlockStatRenderContextMockitoMatcher(expectedContext))
}
