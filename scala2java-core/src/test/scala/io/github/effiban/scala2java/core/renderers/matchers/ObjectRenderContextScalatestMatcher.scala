package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.ObjectRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ObjectRenderContextScalatestMatcher(expectedContext: ObjectRenderContext) extends Matcher[ObjectRenderContext] {

  override def apply(actualContext: ObjectRenderContext): MatchResult = {
    val matches = javaModifiersMatch(actualContext) &&
      javaTypeKeywordMatches(actualContext) &&
      maybeInheritanceKeywordMatches(actualContext) &&
      bodyContextsMatch(actualContext)


    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def javaTypeKeywordMatches(actualContext: ObjectRenderContext): Boolean = {
    actualContext.javaTypeKeyword == expectedContext.javaTypeKeyword
  }

  private def javaModifiersMatch(actualContext: ObjectRenderContext) = {
    actualContext.javaModifiers == expectedContext.javaModifiers
  }

  private def maybeInheritanceKeywordMatches(actualContext: ObjectRenderContext) = {
    actualContext.maybeInheritanceKeyword == expectedContext.maybeInheritanceKeyword
  }

  private def bodyContextsMatch(actualContext: ObjectRenderContext): Boolean = {
    true // TODO
  }

}

object ObjectRenderContextScalatestMatcher {
  def equalObjectRenderContext(expectedContext: ObjectRenderContext): ObjectRenderContextScalatestMatcher =
    new ObjectRenderContextScalatestMatcher(expectedContext)
}

