package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.{BlockTermFunctionTraversalResult, SingleTermFunctionTraversalResult, TermFunctionTraversalResult}
import org.scalatest.matchers.{MatchResult, Matcher}

class TermFunctionTraversalResultScalatestMatcher(expectedTraversalResult: TermFunctionTraversalResult)
  extends Matcher[TermFunctionTraversalResult] {

  override def apply(actualTraversalResult: TermFunctionTraversalResult): MatchResult = {
    val matches = matchesByType(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def matchesByType(actualTraversalResult: TermFunctionTraversalResult) =
    (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: SingleTermFunctionTraversalResult, expectedResult: SingleTermFunctionTraversalResult) =>
        new SingleTermFunctionTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualResult: BlockTermFunctionTraversalResult, expectedResult: BlockTermFunctionTraversalResult) =>
        new BlockTermFunctionTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case _ => false
    }
}

object TermFunctionTraversalResultScalatestMatcher {
  def equalTermFunctionTraversalResult(expectedResult: TermFunctionTraversalResult) =
    new TermFunctionTraversalResultScalatestMatcher(expectedResult)
}


