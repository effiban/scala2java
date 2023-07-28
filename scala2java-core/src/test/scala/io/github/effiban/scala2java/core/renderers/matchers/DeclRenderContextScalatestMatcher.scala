package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.DeclRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class DeclRenderContextScalatestMatcher(expectedContext: DeclRenderContext) extends Matcher[DeclRenderContext] {
  override def apply(actualContext: DeclRenderContext): MatchResult = {
    val matches = true // TODO

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DeclRenderContextScalatestMatcher {
  def equalDeclRenderContext(expectedContext: DeclRenderContext): DeclRenderContextScalatestMatcher =
    new DeclRenderContextScalatestMatcher(expectedContext)
}
