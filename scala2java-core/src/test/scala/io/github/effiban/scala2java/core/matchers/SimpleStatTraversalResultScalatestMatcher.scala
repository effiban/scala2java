package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.SimpleStatTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class SimpleStatTraversalResultScalatestMatcher(expectedTraversalResult: SimpleStatTraversalResult)
  extends Matcher[SimpleStatTraversalResult] {

  override def apply(actualTraversalResult: SimpleStatTraversalResult): MatchResult = {
    val matches = actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

