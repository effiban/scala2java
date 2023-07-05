package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockTraversalResultMockitoMatcher(expectedTraversalResult: BlockTraversalResult)
  extends ArgumentMatcher[BlockTraversalResult] {

  override def matches(actualTraversalResult: BlockTraversalResult): Boolean = {
    nonLastStatsMatch(actualTraversalResult) &&
      maybeLastStatResultMatches(actualTraversalResult) &&
      maybeInitMatches(actualTraversalResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def nonLastStatsMatch(actualTraversalResult: BlockTraversalResult): Boolean = {
    actualTraversalResult.nonLastStats.structure == expectedTraversalResult.nonLastStats.structure
  }

  private def maybeLastStatResultMatches(actualTraversalResult: BlockTraversalResult): Boolean = {
    (actualTraversalResult.maybeLastStatResult, expectedTraversalResult.maybeLastStatResult) match {
      case (Some(actualResult), Some(expectedResult)) =>
        new BlockStatTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case (None, None) => true
      case _ => false
    }
  }

  private def maybeInitMatches(actualTraversalResult: BlockTraversalResult): Boolean = {
    actualTraversalResult.maybeInit.structure == expectedTraversalResult.maybeInit.structure
  }

}

object BlockTraversalResultMockitoMatcher {
  def eqBlockTraversalResult(expectedTraversalResult: BlockTraversalResult): BlockTraversalResult =
    argThat(new BlockTraversalResultMockitoMatcher(expectedTraversalResult))
}

