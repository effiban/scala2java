package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockTermFunctionTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockTermFunctionTraversalResultScalatestMatcher(expectedTraversalResult: BlockTermFunctionTraversalResult)
  extends Matcher[BlockTermFunctionTraversalResult] {

  override def apply(actualTraversalResult: BlockTermFunctionTraversalResult): MatchResult = {
    val matches = paramListsMatch(actualTraversalResult) && bodiesMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def paramListsMatch(actualTraversalResult: BlockTermFunctionTraversalResult) = {
    actualTraversalResult.params.structure == expectedTraversalResult.params.structure
  }

  private def bodiesMatch(actualTraversalResult: BlockTermFunctionTraversalResult) = {
    actualTraversalResult.bodyResult.block.structure == expectedTraversalResult.bodyResult.block.structure
  }
}

