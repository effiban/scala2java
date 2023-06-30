package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.{BlockStatRenderContext, IfRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockStatRenderContextMatcher(expectedContext: BlockStatRenderContext) extends ArgumentMatcher[BlockStatRenderContext] {

  override def matches(actualContext: BlockStatRenderContext): Boolean = {
    matchesByType(actualContext)
  }

  private def matchesByType(actualContext: BlockStatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualContext: SimpleBlockStatRenderContext, expectedContext: SimpleBlockStatRenderContext) =>
        actualContext == expectedContext
      case (actualContext: IfRenderContext, expectedContext: IfRenderContext) =>
        new IfRenderContextMatcher(expectedContext).matches(actualContext)
      case (actualContext: TryRenderContext, expectedContext: TryRenderContext) =>
        new TryRenderContextMatcher(expectedContext).matches(actualContext)
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockStatRenderContextMatcher {
  def eqBlockStatRenderContext(expectedContext: BlockStatRenderContext): BlockStatRenderContext = argThat(new BlockStatRenderContextMatcher(expectedContext))
}
