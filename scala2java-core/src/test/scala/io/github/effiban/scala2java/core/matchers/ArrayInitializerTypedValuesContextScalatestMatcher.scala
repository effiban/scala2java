package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArrayInitializerTypedValuesContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ArrayInitializerTypedValuesContextScalatestMatcher(expectedContext: ArrayInitializerTypedValuesContext) extends Matcher[ArrayInitializerTypedValuesContext] {

  override def apply(actualContext: ArrayInitializerTypedValuesContext): MatchResult = {
    val matches = typeMatches(actualContext) && valuesMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def typeMatches(actualContext: ArrayInitializerTypedValuesContext) = {
    actualContext.tpe.structure == expectedContext.tpe.structure
  }

  private def valuesMatch(actualContext: ArrayInitializerTypedValuesContext) = {
    actualContext.values.structure == expectedContext.values.structure
  }
}

object ArrayInitializerTypedValuesContextScalatestMatcher {
  def equalArrayInitializerTypedValuesContext(expectedContext: ArrayInitializerTypedValuesContext): Matcher[ArrayInitializerTypedValuesContext] =
    new ArrayInitializerTypedValuesContextScalatestMatcher(expectedContext)
}

