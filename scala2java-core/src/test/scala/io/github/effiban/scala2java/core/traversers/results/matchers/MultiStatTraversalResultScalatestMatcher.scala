package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.MultiStatTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class MultiStatTraversalResultScalatestMatcher(expectedTraversalResult: MultiStatTraversalResult)
  extends Matcher[MultiStatTraversalResult] {

  override def apply(actualTraversalResult: MultiStatTraversalResult): MatchResult = {
    val matches = sizeMatches(actualTraversalResult) && statResultsMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def sizeMatches(actualTraversalResult: MultiStatTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: MultiStatTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object MultiStatTraversalResultScalatestMatcher {
  def equalMultiStatTraversalResult(expectedTraversalResult: MultiStatTraversalResult): Matcher[MultiStatTraversalResult] =
    new MultiStatTraversalResultScalatestMatcher(expectedTraversalResult)
}

