package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class IfTraversalResultScalatestMatcher(expectedTraversalResult: IfTraversalResult)
  extends Matcher[IfTraversalResult] {

  override def apply(actualTraversalResult: IfTraversalResult): MatchResult = {
    val matches = theIfMatches(actualTraversalResult) &&
      actualTraversalResult.uncertainReturn == expectedTraversalResult.uncertainReturn

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def theIfMatches(actualTraversalResult: IfTraversalResult) = {
    actualTraversalResult.`if`.structure == expectedTraversalResult.`if`.structure
  }
}

object IfTraversalResultScalatestMatcher {
  def equalIfTraversalResult(expectedTraversalResult: IfTraversalResult): Matcher[IfTraversalResult] =
    new IfTraversalResultScalatestMatcher(expectedTraversalResult)
}

