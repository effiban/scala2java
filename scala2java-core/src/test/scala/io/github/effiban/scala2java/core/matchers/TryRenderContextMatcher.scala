package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TryRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class TryRenderContextMatcher(expectedContext: TryRenderContext) extends ArgumentMatcher[TryRenderContext] {

  override def matches(actualContext: TryRenderContext): Boolean = {
    exprContextMatches(actualContext) && catchContextsMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def exprContextMatches(actualContext: TryRenderContext) = {
    new BlockRenderContextMatcher(expectedContext.exprContext).matches(actualContext.exprContext)
  }

  private def catchContextsMatch(actualContext: TryRenderContext) = {
    expectedContext.catchContexts.indices.forall(idx => catchContextMatches(actualContext, idx))
  }

  private def catchContextMatches(actualTryContext: TryRenderContext, idx: Int): Boolean = {
    if (idx >= actualTryContext.catchContexts.length) {
      return false;
    }
    val expectedCatchContext = expectedContext.catchContexts(idx)
    val actualCatchContext = actualTryContext.catchContexts(idx)
    new BlockRenderContextMatcher(expectedCatchContext).matches(actualCatchContext)
  }
}

object TryRenderContextMatcher {
  def eqTryRenderContext(expectedContext: TryRenderContext): TryRenderContext = argThat(new TryRenderContextMatcher(expectedContext))
}
