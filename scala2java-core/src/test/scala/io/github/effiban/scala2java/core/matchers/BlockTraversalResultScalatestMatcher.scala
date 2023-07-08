package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockTraversalResultScalatestMatcher(expectedTraversalResult: BlockTraversalResult)
  extends Matcher[BlockTraversalResult] {

  override def apply(actualTraversalResult: BlockTraversalResult): MatchResult = {
    val matches = nonLastStatsMatch(actualTraversalResult) &&
      maybeLastStatResultMatches(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def nonLastStatsMatch(actualTraversalResult: BlockTraversalResult): Boolean = {
    actualTraversalResult.nonLastStats.structure == expectedTraversalResult.nonLastStats.structure
  }

  private def maybeLastStatResultMatches(actualTraversalResult: BlockTraversalResult): Boolean = {
    (actualTraversalResult.maybeLastStatResult, expectedTraversalResult.maybeLastStatResult) match {
      case (Some(actualResult), Some(expectedResult)) =>
        new BlockStatTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (None, None) => true
      case _ => false
    }
  }
}

object BlockTraversalResultScalatestMatcher {
  def equalBlockTraversalResult(expectedTraversalResult: BlockTraversalResult): Matcher[BlockTraversalResult] =
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult)
}

