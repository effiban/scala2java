package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.TermParamTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class TermParamTraversalResultScalatestMatcher(expectedTraversalResult: TermParamTraversalResult)
  extends Matcher[TermParamTraversalResult] {

  override def apply(actualTraversalResult: TermParamTraversalResult): MatchResult = {
    val matches = termParamsMatch(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def termParamsMatch(actualTraversalResult: TermParamTraversalResult) = {
    actualTraversalResult.termParam.structure == expectedTraversalResult.termParam.structure
  }

  private def javaModifiersMatch(actualTraversalResult: TermParamTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

}

object TermParamTraversalResultScalatestMatcher {
  def equalTermParamTraversalResult(expectedTraversalResult: TermParamTraversalResult): Matcher[TermParamTraversalResult] =
    new TermParamTraversalResultScalatestMatcher(expectedTraversalResult)
}

