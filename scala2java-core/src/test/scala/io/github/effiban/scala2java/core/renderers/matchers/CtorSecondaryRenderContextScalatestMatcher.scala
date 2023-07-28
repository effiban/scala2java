package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.CtorSecondaryRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class CtorSecondaryRenderContextScalatestMatcher(expectedContext: CtorSecondaryRenderContext) extends Matcher[CtorSecondaryRenderContext] {

  override def apply(actualContext: CtorSecondaryRenderContext): MatchResult = {
    val matches = classNamesMatch(actualContext) && javaModifiersMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def classNamesMatch(actualContext: CtorSecondaryRenderContext) = {
    actualContext.className.structure == expectedContext.className.structure
  }

  private def javaModifiersMatch(actualContext: CtorSecondaryRenderContext): Boolean =
    actualContext.javaModifiers == expectedContext.javaModifiers

  override def toString: String = s"Matcher for: $expectedContext"
}

object CtorSecondaryRenderContextScalatestMatcher {
  def equalCtorSecondaryRenderContext(expectedContext: CtorSecondaryRenderContext): CtorSecondaryRenderContextScalatestMatcher =
    new CtorSecondaryRenderContextScalatestMatcher(expectedContext)
}

