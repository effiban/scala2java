package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class IfTraversalResultMockitoMatcher(expectedTraversalResult: IfTraversalResult) extends ArgumentMatcher[IfTraversalResult] {

  override def matches(actualTraversalResult: IfTraversalResult): Boolean = {
    condMatches(actualTraversalResult) &&
      thenpResultMatches(actualTraversalResult) &&
      maybeElsepResultMatches(actualTraversalResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def condMatches(actualTraversalResult: IfTraversalResult) = {
    actualTraversalResult.cond.structure == expectedTraversalResult.cond.structure
  }

  private def thenpResultMatches(actualTraversalResult: IfTraversalResult): Boolean = {
    new BlockTraversalResultMockitoMatcher(expectedTraversalResult.thenpResult).matches(actualTraversalResult.thenpResult)
  }

  private def maybeElsepResultMatches(actualTraversalResult: IfTraversalResult): Boolean = {
    (actualTraversalResult.maybeElsepResult, expectedTraversalResult.maybeElsepResult) match {
      case (Some(actualResult), Some(expectedResult)) => new BlockTraversalResultMockitoMatcher(expectedResult).matches(actualResult)
      case (None, None) => true
      case _ => false
    }
  }
}

object IfTraversalResultMockitoMatcher {
  def eqIfTraversalResult(expectedTraversalResult: IfTraversalResult): IfTraversalResult =
    argThat(new IfTraversalResultMockitoMatcher(expectedTraversalResult))
}

