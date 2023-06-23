package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.CatchHandlerTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class CatchHandlerTraversalResultScalatestMatcher(expectedTraversalResult: CatchHandlerTraversalResult)
  extends Matcher[CatchHandlerTraversalResult] {

  override def apply(actualTraversalResult: CatchHandlerTraversalResult): MatchResult = {
    val matches = catchCaseMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def catchCaseMatches(actualTraversalResult: CatchHandlerTraversalResult) = {
    actualTraversalResult.catchCase.structure == expectedTraversalResult.catchCase.structure
  }
}

object CatchHandlerTraversalResultScalatestMatcher {
  def equalCatchHandlerTraversalResult(expectedTraversalResult: CatchHandlerTraversalResult): Matcher[CatchHandlerTraversalResult] =
    new CatchHandlerTraversalResultScalatestMatcher(expectedTraversalResult)
}

