package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.CatchHandlerTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class CatchHandlerTraversalResultScalatestMatcher(expectedTraversalResult: CatchHandlerTraversalResult)
  extends Matcher[CatchHandlerTraversalResult] {

  override def apply(actualTraversalResult: CatchHandlerTraversalResult): MatchResult = {
    val matches = catchArgMatches(actualTraversalResult) && bodyMatches(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  private def bodyMatches(actualTraversalResult: CatchHandlerTraversalResult): Boolean = {
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult.bodyResult)(actualTraversalResult.bodyResult).matches
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def catchArgMatches(actualTraversalResult: CatchHandlerTraversalResult) = {
    actualTraversalResult.pat.structure == expectedTraversalResult.pat.structure
  }
}

object CatchHandlerTraversalResultScalatestMatcher {
  def equalCatchHandlerTraversalResult(expectedTraversalResult: CatchHandlerTraversalResult): Matcher[CatchHandlerTraversalResult] =
    new CatchHandlerTraversalResultScalatestMatcher(expectedTraversalResult)
}

