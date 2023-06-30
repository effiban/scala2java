package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class IfTraversalResultScalatestMatcher(expectedTraversalResult: IfTraversalResult)
  extends Matcher[IfTraversalResult] {

  override def apply(actualTraversalResult: IfTraversalResult): MatchResult = {
    val matches = condMatches(actualTraversalResult) &&
      thenpResultMatches(actualTraversalResult) &&
      maybeElsepResultMatches(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def condMatches(actualTraversalResult: IfTraversalResult) = {
    actualTraversalResult.cond.structure == expectedTraversalResult.cond.structure
  }

  private def thenpResultMatches(actualTraversalResult: IfTraversalResult): Boolean = {
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult.thenpResult)(actualTraversalResult.thenpResult).matches
  }

  private def maybeElsepResultMatches(actualTraversalResult: IfTraversalResult): Boolean = {
    (actualTraversalResult.maybeElsepResult, expectedTraversalResult.maybeElsepResult) match {
      case (Some(actualResult), Some(expectedResult)) => new BlockTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (None, None) => true
      case _ => false
    }
  }
}

object IfTraversalResultScalatestMatcher {
  def equalIfTraversalResult(expectedTraversalResult: IfTraversalResult): Matcher[IfTraversalResult] =
    new IfTraversalResultScalatestMatcher(expectedTraversalResult)
}

