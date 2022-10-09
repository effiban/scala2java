package io.github.effiban.scala2java.matchers

import io.github.effiban.scala2java.contexts.ArrayInitializerSizeContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ArrayInitializerSizeContextScalatestMatcher(expectedContext: ArrayInitializerSizeContext) extends Matcher[ArrayInitializerSizeContext] {

  override def apply(actualContext: ArrayInitializerSizeContext): MatchResult = {
    val matches = typeMatches(actualContext) && sizeMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def typeMatches(actualContext: ArrayInitializerSizeContext) = {
    actualContext.tpe.structure == expectedContext.tpe.structure
  }

  private def sizeMatches(actualContext: ArrayInitializerSizeContext) = {
    actualContext.size.structure == expectedContext.size.structure
  }
}

object ArrayInitializerSizeContextScalatestMatcher {
  def equalArrayInitializerSizeContext(expectedContext: ArrayInitializerSizeContext): Matcher[ArrayInitializerSizeContext] =
    new ArrayInitializerSizeContextScalatestMatcher(expectedContext)
}

