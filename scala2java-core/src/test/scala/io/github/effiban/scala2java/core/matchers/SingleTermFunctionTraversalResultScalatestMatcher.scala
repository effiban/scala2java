package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.SingleTermFunctionTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class SingleTermFunctionTraversalResultScalatestMatcher(expectedTraversalResult: SingleTermFunctionTraversalResult)
  extends Matcher[SingleTermFunctionTraversalResult] {

  override def apply(actualTraversalResult: SingleTermFunctionTraversalResult): MatchResult = {
    val matches = actualTraversalResult.function.structure == expectedTraversalResult.function.structure

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

