package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Init

class BlockRenderContextScalatestMatcher(expectedContext: BlockRenderContext) extends Matcher[BlockRenderContext] {

  override def apply(actualContext: BlockRenderContext): MatchResult = {
    val matches = lastStatContextMatches(actualContext) && maybeInitMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def lastStatContextMatches(actualContext: BlockRenderContext) = {
    new BlockStatRenderContextScalatestMatcher(expectedContext.lastStatContext)(actualContext.lastStatContext).matches
  }

  private def maybeInitMatches(actualContext: BlockRenderContext) = {
    new OptionMatcher[Init](expectedContext.maybeInit, new TreeMatcher[Init](_)).matches(actualContext.maybeInit)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockRenderContextScalatestMatcher {
  def equalBlockRenderContext(expectedContext: BlockRenderContext): BlockRenderContextScalatestMatcher =
    new BlockRenderContextScalatestMatcher(expectedContext)
}

