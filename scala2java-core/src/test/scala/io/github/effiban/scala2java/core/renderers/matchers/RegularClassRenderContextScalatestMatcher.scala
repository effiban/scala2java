package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.RegularClassRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class RegularClassRenderContextScalatestMatcher(expectedContext: RegularClassRenderContext) extends Matcher[RegularClassRenderContext] {

  override def apply(actualContext: RegularClassRenderContext): MatchResult = {
    val matches = javaModifiersMatch(actualContext) &&
      javaTypeKeywordMatches(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      permittedSubTypeNamesMatch(actualContext) &&
      bodyContextsMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaTypeKeywordMatches(actualContext: RegularClassRenderContext): Boolean = {
    actualContext.javaTypeKeyword == expectedContext.javaTypeKeyword
  }

  private def javaModifiersMatch(actualContext: RegularClassRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: RegularClassRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def permittedSubTypeNamesMatch(actualContext: RegularClassRenderContext): Boolean = {
    actualContext.permittedSubTypeNames.structure == expectedContext.permittedSubTypeNames.structure
  }

  private def bodyContextsMatch(actualContext: RegularClassRenderContext): Boolean = {
    new TemplateBodyRenderContextScalatestMatcher(expectedContext.bodyContext)(actualContext.bodyContext).matches
  }

}

object RegularClassRenderContextScalatestMatcher {
  def equalRegularClassRenderContext(expectedContext: RegularClassRenderContext): RegularClassRenderContextScalatestMatcher =
    new RegularClassRenderContextScalatestMatcher(expectedContext)
}

