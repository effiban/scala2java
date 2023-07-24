package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.SourceRenderContext
import io.github.effiban.scala2java.test.utils.matchers.TreeKeyedMapScalatestMatcher
import org.scalatest.matchers.{MatchResult, Matcher}

class SourceRenderContextScalatestMatcher(expectedContext: SourceRenderContext) extends Matcher[SourceRenderContext] {

  override def apply(actualContext: SourceRenderContext): MatchResult = {
    val mapMatcher = new TreeKeyedMapScalatestMatcher(expectedContext.statContextMap, new StatRenderContextScalatestMatcher(_))
    val matches = mapMatcher(actualContext.statContextMap).matches

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object SourceRenderContextScalatestMatcher {
  def equalSourceRenderContext(expectedContext: SourceRenderContext): SourceRenderContextScalatestMatcher =
    new SourceRenderContextScalatestMatcher(expectedContext)
}

