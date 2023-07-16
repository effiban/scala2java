package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.renderers.contexts.ArrayInitializerSizeRenderContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ArrayInitializerSizeRenderContextScalatestMatcher(expectedContext: ArrayInitializerSizeRenderContext) 
  extends Matcher[ArrayInitializerSizeRenderContext] {

  override def apply(actualContext: ArrayInitializerSizeRenderContext): MatchResult = {
    val matches = typeMatches(actualContext) && sizeMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def typeMatches(actualContext: ArrayInitializerSizeRenderContext) = {
    actualContext.tpe.structure == expectedContext.tpe.structure
  }

  private def sizeMatches(actualContext: ArrayInitializerSizeRenderContext) = {
    actualContext.size.structure == expectedContext.size.structure
  }
}

object ArrayInitializerSizeRenderContextScalatestMatcher {
  def equalArrayInitializerSizeRenderContext(expectedContext: ArrayInitializerSizeRenderContext): Matcher[ArrayInitializerSizeRenderContext] =
    new ArrayInitializerSizeRenderContextScalatestMatcher(expectedContext)
}

