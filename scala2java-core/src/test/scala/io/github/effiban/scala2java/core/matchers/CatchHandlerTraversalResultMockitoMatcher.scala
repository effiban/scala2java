package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.CatchHandlerTraversalResult
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class CatchHandlerTraversalResultMockitoMatcher(expectedTraversalResult: CatchHandlerTraversalResult)
  extends ArgumentMatcher[CatchHandlerTraversalResult] {

  override def matches(actualTraversalResult: CatchHandlerTraversalResult): Boolean = {
    catchArgMatches(actualTraversalResult) && bodyMatches(actualTraversalResult)
  }

  private def bodyMatches(actualTraversalResult: CatchHandlerTraversalResult): Boolean = {
    new BlockTraversalResultMockitoMatcher(expectedTraversalResult.bodyResult).matches(actualTraversalResult.bodyResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def catchArgMatches(actualTraversalResult: CatchHandlerTraversalResult) = {
    actualTraversalResult.pat.structure == expectedTraversalResult.pat.structure
  }
}

object CatchHandlerTraversalResultMockitoMatcher {
  def eqCatchHandlerTraversalResult(expectedTraversalResult: CatchHandlerTraversalResult): CatchHandlerTraversalResult =
    argThat(new CatchHandlerTraversalResultMockitoMatcher(expectedTraversalResult))
}

