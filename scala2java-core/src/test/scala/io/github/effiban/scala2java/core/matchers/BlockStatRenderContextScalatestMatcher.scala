package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.{BlockStatRenderContext, IfRenderContext, SimpleBlockStatRenderContext, TryRenderContext}
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockStatRenderContextScalatestMatcher(expectedContext: BlockStatRenderContext) extends Matcher[BlockStatRenderContext] {

  override def apply(actualContext: BlockStatRenderContext): MatchResult = {
    val matches = matchesByType(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def matchesByType(actualContext: BlockStatRenderContext): Boolean = {
    (actualContext, expectedContext) match {
      case (actualContext: SimpleBlockStatRenderContext, expectedContext: SimpleBlockStatRenderContext) =>
        actualContext == expectedContext
      case (actualContext: IfRenderContext, expectedContext: IfRenderContext) =>
        new IfRenderContextScalatestMatcher(expectedContext)(actualContext).matches
      case (actualContext: TryRenderContext, expectedContext: TryRenderContext) =>
        new TryRenderContextScalatestMatcher(expectedContext)(actualContext).matches
      case _ => false
    }
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockStatRenderContextScalatestMatcher {
  def equalBlockStatRenderContext(expectedContext: BlockStatRenderContext): BlockStatRenderContextScalatestMatcher =
    new BlockStatRenderContextScalatestMatcher(expectedContext)
}
