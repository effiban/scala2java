package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.SimpleBlockStatTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class SimpleBlockStatTraversalResultScalatestMatcher(expectedTraversalResult: SimpleBlockStatTraversalResult)
  extends Matcher[SimpleBlockStatTraversalResult] {

  override def apply(actualTraversalResult: SimpleBlockStatTraversalResult): MatchResult = {
    val matches = statMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def statMatches(actualTraversalResult: SimpleBlockStatTraversalResult) = {
    actualTraversalResult.stat.structure == expectedTraversalResult.stat.structure
  }
}

