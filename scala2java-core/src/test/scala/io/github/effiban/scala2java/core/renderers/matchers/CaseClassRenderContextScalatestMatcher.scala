package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.CaseClassRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class CaseClassRenderContextScalatestMatcher(expectedContext: CaseClassRenderContext) extends Matcher[CaseClassRenderContext] {

  override def apply(actualContext: CaseClassRenderContext): MatchResult = {
    val matches = javaModifiersMatch(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      bodyContextsMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaModifiersMatch(actualContext: CaseClassRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: CaseClassRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def bodyContextsMatch(actualContext: CaseClassRenderContext): Boolean = {
    new TemplateBodyRenderContextScalatestMatcher(expectedContext.bodyContext)(actualContext.bodyContext).matches
  }

}

object CaseClassRenderContextScalatestMatcher {
  def equalCaseClassRenderContext(expectedContext: CaseClassRenderContext): CaseClassRenderContextScalatestMatcher =
    new CaseClassRenderContextScalatestMatcher(expectedContext)
}

