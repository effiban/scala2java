package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.IfRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class IfRenderContextScalatestMatcher(expectedContext: IfRenderContext) extends Matcher[IfRenderContext] {

  override def apply(actualContext: IfRenderContext): MatchResult = {
    val matches = thenContextMatches(actualContext) && elseContextMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def thenContextMatches(actualContext: IfRenderContext) = {
    new BlockRenderContextScalatestMatcher(expectedContext.thenContext)(actualContext.thenContext).matches
  }

  private def elseContextMatches(actualContext: IfRenderContext) = {
    new BlockRenderContextScalatestMatcher(expectedContext.elseContext)(actualContext.elseContext).matches
  }

}

object IfRenderContextScalatestMatcher {
  def equalIfRenderContext(expectedContext: IfRenderContext): IfRenderContextScalatestMatcher =
    new IfRenderContextScalatestMatcher(expectedContext)
}
