package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TryRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TryRenderContextMockitoMatcher(expectedContext: TryRenderContext) extends ArgumentMatcher[TryRenderContext] {

  override def matches(actualContext: TryRenderContext): Boolean = {
    exprContextMatches(actualContext) && catchContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def exprContextMatches(actualContext: TryRenderContext) = {
    new BlockRenderContextMockitoMatcher(expectedContext.exprContext).matches(actualContext.exprContext)
  }

  private def catchContextsMatch(actualContext: TryRenderContext) = {
    expectedContext.catchContexts.indices.forall(idx => catchContextMatches(actualContext, idx))
  }

  private def catchContextMatches(actualTryContext: TryRenderContext, idx: Int): Boolean = {
    if (idx >= actualTryContext.catchContexts.length) {
      false
    } else {
      val expectedCatchContext = expectedContext.catchContexts(idx)
      val actualCatchContext = actualTryContext.catchContexts(idx)
      new BlockRenderContextMockitoMatcher(expectedCatchContext).matches(actualCatchContext)
    }
  }
}

object TryRenderContextMockitoMatcher {
  def eqTryRenderContext(expectedContext: TryRenderContext): TryRenderContext = argThat(new TryRenderContextMockitoMatcher(expectedContext))
}
