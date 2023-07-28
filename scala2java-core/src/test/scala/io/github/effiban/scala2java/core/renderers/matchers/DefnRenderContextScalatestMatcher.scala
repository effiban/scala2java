package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.{DefnRenderContext, TraitRenderContext}
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnRenderContextScalatestMatcher(expectedContext: DefnRenderContext) extends Matcher[DefnRenderContext] {
  override def apply(actualContext: DefnRenderContext): MatchResult = {
    val matches = (actualContext, expectedContext) match {
      case (actualTraitContext: TraitRenderContext, expectedTraitContext: TraitRenderContext) =>
        new TraitRenderContextScalatestMatcher(expectedTraitContext)(actualTraitContext).matches
      // TODO support the rest of the non-trivial hierarchy
      case (anActualContext, anExpectedContext) => anActualContext == anExpectedContext
    }

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
