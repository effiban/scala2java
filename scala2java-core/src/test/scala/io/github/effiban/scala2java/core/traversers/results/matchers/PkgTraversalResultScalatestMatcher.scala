package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.matchers.SealedHierarchiesScalatestMatcher
import io.github.effiban.scala2java.core.traversers.results.PkgTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class PkgTraversalResultScalatestMatcher(expectedTraversalResult: PkgTraversalResult) extends Matcher[PkgTraversalResult] {

  override def apply(actualTraversalResult: PkgTraversalResult): MatchResult = {
    val matches = pkgRefMatches(actualTraversalResult) &&
      numStatsMatch(actualTraversalResult) &&
      statResultsMatch(actualTraversalResult) &&
      sealedHierarchiesMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def pkgRefMatches(actualTraversalResult: PkgTraversalResult) = {
    actualTraversalResult.pkgRef.structure == expectedTraversalResult.pkgRef.structure
  }

  private def numStatsMatch(actualTraversalResult: PkgTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: PkgTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }

  private def sealedHierarchiesMatch(actualTraversalResult: PkgTraversalResult): Boolean = {
    new SealedHierarchiesScalatestMatcher(expectedTraversalResult.sealedHierarchies)(actualTraversalResult.sealedHierarchies).matches
  }
}

object PkgTraversalResultScalatestMatcher {
  def equalPkgTraversalResult(expectedTraversalResult: PkgTraversalResult): Matcher[PkgTraversalResult] =
    new PkgTraversalResultScalatestMatcher(expectedTraversalResult)
}

