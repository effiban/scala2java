package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.DefnRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnRenderContextScalatestMatcher(expectedContext: DefnRenderContext) extends Matcher[DefnRenderContext] {
  override def apply(actualContext: DefnRenderContext): MatchResult = {
    val matches = true // TODO

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DefnRenderContextScalatestMatcher {
  def equalDefnRenderContext(expectedContext: DefnRenderContext): DefnRenderContextScalatestMatcher =
    new DefnRenderContextScalatestMatcher(expectedContext)
}
