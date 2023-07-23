package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.{PkgTraversalResult, PopulatedStatTraversalResult, StatWithJavaModifiersTraversalResult}
import org.scalatest.matchers.{MatchResult, Matcher}

class PopulatedStatTraversalResultScalatestMatcher(expectedTraversalResult: PopulatedStatTraversalResult) extends Matcher[PopulatedStatTraversalResult] {

  override def apply(actualTraversalResult: PopulatedStatTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: StatWithJavaModifiersTraversalResult, expectedResult: StatWithJavaModifiersTraversalResult) =>
        new StatWithJavaModifiersTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualResult: PkgTraversalResult, expectedResult: PkgTraversalResult) =>
        new PkgTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (anActualTraversalResult, anExpectedTraversalResult) => anActualTraversalResult == anExpectedTraversalResult
    }

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}
