package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.BlockStatTraversalResult
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchersSugar.argThat

class BlockStatTraversalResultMockitoMatcher(expectedTraversalResult: BlockStatTraversalResult) extends ArgumentMatcher[BlockStatTraversalResult] {

  override def matches(actualTraversalResult: BlockStatTraversalResult): Boolean = {
    statMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def statMatches(actualTraversalResult: BlockStatTraversalResult) = {
    actualTraversalResult.stat.structure == expectedTraversalResult.stat.structure
  }
}

object BlockStatTraversalResultMockitoMatcher {
  def eqBlockStatTraversalResult(expectedTraversalResult: BlockStatTraversalResult): BlockStatTraversalResult =
    argThat(new BlockStatTraversalResultMockitoMatcher(expectedTraversalResult))
}

