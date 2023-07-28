package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.SourceTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class SourceTraversalResultScalatestMatcher(expectedTraversalResult: SourceTraversalResult) extends Matcher[SourceTraversalResult] {

  override def apply(actualTraversalResult: SourceTraversalResult): MatchResult = {
    val matches = numStatsMatch(actualTraversalResult) &&
      statResultsMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def numStatsMatch(actualTraversalResult: SourceTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: SourceTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object SourceTraversalResultScalatestMatcher {
  def equalSourceTraversalResult(expectedTraversalResult: SourceTraversalResult): Matcher[SourceTraversalResult] =
    new SourceTraversalResultScalatestMatcher(expectedTraversalResult)
}

