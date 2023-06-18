package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockStatTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockStatTraversalResultScalatestMatcher(expectedTraversalResult: BlockStatTraversalResult)
  extends Matcher[BlockStatTraversalResult] {

  override def apply(actualTraversalResult: BlockStatTraversalResult): MatchResult = {
    val matches = statMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def statMatches(actualTraversalResult: BlockStatTraversalResult) = {
    actualTraversalResult.stat.structure == expectedTraversalResult.stat.structure
  }
}

object BlockStatTraversalResultScalatestMatcher {
  def equalBlockStatTraversalResult(expectedTraversalResult: BlockStatTraversalResult): Matcher[BlockStatTraversalResult] =
    new BlockStatTraversalResultScalatestMatcher(expectedTraversalResult)
}

