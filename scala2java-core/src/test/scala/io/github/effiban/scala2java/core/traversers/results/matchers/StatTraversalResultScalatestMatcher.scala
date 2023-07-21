package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.{PopulatedStatTraversalResult, StatTraversalResult, StatWithJavaModifiersTraversalResult}
import org.scalatest.matchers.{MatchResult, Matcher}

class StatTraversalResultScalatestMatcher(expectedTraversalResult: StatTraversalResult) extends Matcher[StatTraversalResult] {

  override def apply(actualTraversalResult: StatTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: StatWithJavaModifiersTraversalResult, expectedResult: StatWithJavaModifiersTraversalResult) =>
        new StatWithJavaModifiersTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case  (actualResult: PopulatedStatTraversalResult, expectedResult: PopulatedStatTraversalResult) =>
        actualResult.tree.structure == expectedResult.tree.structure
      case (anActualTraversalResult, anExpectedTraversalResult) => anActualTraversalResult == anExpectedTraversalResult
    }

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

object StatTraversalResultScalatestMatcher {
  def equalStatTraversalResult(expectedTraversalResult: StatTraversalResult): Matcher[StatTraversalResult] =
    new StatTraversalResultScalatestMatcher(expectedTraversalResult)
}

