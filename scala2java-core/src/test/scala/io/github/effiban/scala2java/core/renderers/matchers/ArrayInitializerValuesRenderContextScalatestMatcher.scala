package io.github.effiban.scala2java.core.renderers.matchers

import io.github.effiban.scala2java.core.renderers.contexts.ArrayInitializerValuesRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ArrayInitializerValuesRenderContextScalatestMatcher(expectedContext: ArrayInitializerValuesRenderContext)
  extends Matcher[ArrayInitializerValuesRenderContext] {

  override def apply(actualContext: ArrayInitializerValuesRenderContext): MatchResult = {
    val matches = typeMatches(actualContext) && valuesMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def typeMatches(actualContext: ArrayInitializerValuesRenderContext) = {
    actualContext.tpe.structure == expectedContext.tpe.structure
  }

  private def valuesMatch(actualContext: ArrayInitializerValuesRenderContext) = {
    actualContext.values.structure == expectedContext.values.structure
  }
}

object ArrayInitializerValuesRenderContextScalatestMatcher {
  def equalArrayInitializerValuesRenderContext(expectedContext: ArrayInitializerValuesRenderContext): Matcher[ArrayInitializerValuesRenderContext] =
    new ArrayInitializerValuesRenderContextScalatestMatcher(expectedContext)
}

