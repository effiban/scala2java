package effiban.scala2java.matchers

import effiban.scala2java.contexts.ArrayInitializerValuesContext
import org.scalatest.matchers.{MatchResult, Matcher}

class ArrayInitializerValuesContextScalatestMatcher(expectedContext: ArrayInitializerValuesContext) extends Matcher[ArrayInitializerValuesContext] {

  override def apply(actualContext: ArrayInitializerValuesContext): MatchResult = {
    val matches = maybeTypeMatches(actualContext) && valuesMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  private def maybeTypeMatches(actualContext: ArrayInitializerValuesContext) = {
    actualContext.maybeType.structure == expectedContext.maybeType.structure
  }

  private def valuesMatch(actualContext: ArrayInitializerValuesContext) = {
    actualContext.values.structure == expectedContext.values.structure
  }
}

object ArrayInitializerValuesContextScalatestMatcher {
  def equalArrayInitializerValuesContext(expectedContext: ArrayInitializerValuesContext): Matcher[ArrayInitializerValuesContext] =
    new ArrayInitializerValuesContextScalatestMatcher(expectedContext)
}

