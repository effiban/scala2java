package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results._
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockStatTraversalResultMockitoMatcher(expectedTraversalResult: BlockStatTraversalResult)
  extends ArgumentMatcher[BlockStatTraversalResult] {

  override def matches(actualTraversalResult: BlockStatTraversalResult): Boolean = matchesByType(actualTraversalResult)

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def matchesByType(actualTraversalResult: BlockStatTraversalResult): Boolean = {
    (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: SimpleBlockStatTraversalResult, expectedResult: SimpleBlockStatTraversalResult) =>
        new SimpleBlockStatTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case (actualResult: IfTraversalResult, expectedResult: IfTraversalResult) =>
        new IfTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case (actualResult: TryTraversalResult, expectedResult: TryTraversalResult) =>
        new TryTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case (actualResult: TryWithHandlerTraversalResult, expectedResult: TryWithHandlerTraversalResult) =>
        new TryWithHandlerTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case _ => false
    }
  }
}

object BlockStatTraversalResultMockitoMatcher {
  def eqBlockStatTraversalResult(expectedTraversalResult: BlockStatTraversalResult): BlockStatTraversalResult =
    argThat(new BlockStatTraversalResultMockitoMatcher(expectedTraversalResult))
}

