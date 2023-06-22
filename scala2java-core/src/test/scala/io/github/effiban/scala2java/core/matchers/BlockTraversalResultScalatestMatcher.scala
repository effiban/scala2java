package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockTraversalResultScalatestMatcher(expectedTraversalResult: BlockTraversalResult)
  extends Matcher[BlockTraversalResult] {

  override def apply(actualTraversalResult: BlockTraversalResult): MatchResult = {
    val matches = blockMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def blockMatches(actualTraversalResult: BlockTraversalResult) = {
    actualTraversalResult.block.structure == expectedTraversalResult.block.structure
  }
}

object BlockTraversalResultScalatestMatcher {
  def equalBlockTraversalResult(expectedTraversalResult: BlockTraversalResult): Matcher[BlockTraversalResult] =
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult)
}

