package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockRenderContextScalatestMatcher(expectedContext: BlockRenderContext) extends Matcher[BlockRenderContext] {

  override def apply(actualContext: BlockRenderContext): MatchResult = {
    val matches = lastStatContextMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def lastStatContextMatches(actualContext: BlockRenderContext) = {
    new BlockStatRenderContextScalatestMatcher(expectedContext.lastStatContext)(actualContext.lastStatContext).matches
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockRenderContextScalatestMatcher {
  def equalBlockRenderContext(expectedContext: BlockRenderContext): BlockRenderContextScalatestMatcher =
    new BlockRenderContextScalatestMatcher(expectedContext)
}

