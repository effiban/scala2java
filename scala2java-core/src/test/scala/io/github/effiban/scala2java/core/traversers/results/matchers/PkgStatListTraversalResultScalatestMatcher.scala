package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.matchers.SealedHierarchiesScalatestMatcher
import io.github.effiban.scala2java.core.traversers.results.PkgStatListTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class PkgStatListTraversalResultScalatestMatcher(expectedTraversalResult: PkgStatListTraversalResult)
  extends Matcher[PkgStatListTraversalResult] {

  override def apply(actualTraversalResult: PkgStatListTraversalResult): MatchResult = {
    val matches = numStatsMatch(actualTraversalResult) &&
      statResultsMatch(actualTraversalResult) &&
      sealedHierarchiesMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def numStatsMatch(actualTraversalResult: PkgStatListTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: PkgStatListTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }

  private def sealedHierarchiesMatch(actualTraversalResult: PkgStatListTraversalResult): Boolean = {
    new SealedHierarchiesScalatestMatcher(expectedTraversalResult.sealedHierarchies)(actualTraversalResult.sealedHierarchies).matches
  }
}

object PkgStatListTraversalResultScalatestMatcher {
  def equalPkgStatListTraversalResult(expectedTraversalResult: PkgStatListTraversalResult): Matcher[PkgStatListTraversalResult] =
    new PkgStatListTraversalResultScalatestMatcher(expectedTraversalResult)
}

