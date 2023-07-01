package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.WithJavaModifiersTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class WithJavaModifiersTraversalResultScalatestMatcher(expectedTraversalResult: WithJavaModifiersTraversalResult)
  extends Matcher[WithJavaModifiersTraversalResult] {

  override def apply(actualTraversalResult: WithJavaModifiersTraversalResult): MatchResult = {
    val matches = treeMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def treeMatches(actualTraversalResult: WithJavaModifiersTraversalResult) =
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure

  private def javaModifiersMatch(actualTraversalResult: WithJavaModifiersTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object WithJavaModifiersTraversalResultScalatestMatcher {
  def equalWithJavaModifiersTraversalResult(expectedTraversalResult: WithJavaModifiersTraversalResult): Matcher[WithJavaModifiersTraversalResult] =
    new WithJavaModifiersTraversalResultScalatestMatcher(expectedTraversalResult)
}

