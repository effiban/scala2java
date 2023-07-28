package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results._
import org.scalatest.matchers.{MatchResult, Matcher}

class StatWithJavaModifiersTraversalResultScalatestMatcher(expectedTraversalResult: StatWithJavaModifiersTraversalResult)
  extends Matcher[StatWithJavaModifiersTraversalResult] {

  override def apply(actualTraversalResult: StatWithJavaModifiersTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: DeclTraversalResult, expectedResult: DeclTraversalResult) =>
        new DeclTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualResult: DefnTraversalResult, expectedResult: DefnTraversalResult) =>
        new DefnTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualResult: CtorSecondaryTraversalResult, expectedResult: CtorSecondaryTraversalResult) =>
        new CtorSecondaryTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (anActualTraversalResult, anExpectedTraversalResult) => anActualTraversalResult == anExpectedTraversalResult
    }

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

object StatWithJavaModifiersTraversalResultScalatestMatcher {
  def equalStatWithJavaModifiersTraversalResult(
    expectedTraversalResult: StatWithJavaModifiersTraversalResult) : Matcher[StatWithJavaModifiersTraversalResult] =
    new StatWithJavaModifiersTraversalResultScalatestMatcher(expectedTraversalResult)
}

