package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TryRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class TryRenderContextScalatestMatcher(expectedContext: TryRenderContext) extends Matcher[TryRenderContext] {

  override def apply(actualContext: TryRenderContext): MatchResult = {
    val matches = exprContextMatches(actualContext) && catchContextsMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def exprContextMatches(actualContext: TryRenderContext) = {
    new BlockRenderContextScalatestMatcher(expectedContext.exprContext)(actualContext.exprContext).matches
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
      new BlockRenderContextScalatestMatcher(expectedCatchContext)(actualCatchContext).matches
    }
  }
}

object TryRenderContextScalatestMatcher {
  def equalTryRenderContext(expectedContext: TryRenderContext): TryRenderContextScalatestMatcher =
    new TryRenderContextScalatestMatcher(expectedContext)
}
