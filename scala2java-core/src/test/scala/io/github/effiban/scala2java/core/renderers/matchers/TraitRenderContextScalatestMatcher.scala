package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.TraitRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class TraitRenderContextScalatestMatcher(expectedContext: TraitRenderContext) extends Matcher[TraitRenderContext] {

  override def apply(actualContext: TraitRenderContext): MatchResult = {
    val matches = javaModifiersMatch(actualContext) &&
      permittedSubTypeNamesMatch(actualContext) &&
      bodyContextsMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaModifiersMatch(actualContext: TraitRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def permittedSubTypeNamesMatch(actualContext: TraitRenderContext): Boolean = {
    actualContext.permittedSubTypeNames.structure == expectedContext.permittedSubTypeNames.structure
  }

  private def bodyContextsMatch(actualContext: TraitRenderContext): Boolean = {
    true // TODO
  }

}

object TraitRenderContextScalatestMatcher {
  def equalTraitRenderContext(expectedContext: TraitRenderContext): TraitRenderContextScalatestMatcher =
    new TraitRenderContextScalatestMatcher(expectedContext)
}

