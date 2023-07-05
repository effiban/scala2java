package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.SimpleBlockStatTraversalResult
import org.mockito.ArgumentMatcher

class SimpleBlockStatTraversalResultMockitoMatcher(expectedTraversalResult: SimpleBlockStatTraversalResult)
  extends ArgumentMatcher[SimpleBlockStatTraversalResult] {

  override def matches(actualTraversalResult: SimpleBlockStatTraversalResult): Boolean = {
    statMatches(actualTraversalResult) && actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def statMatches(actualTraversalResult: SimpleBlockStatTraversalResult) = {
    actualTraversalResult.stat.structure == expectedTraversalResult.stat.structure
  }
}

