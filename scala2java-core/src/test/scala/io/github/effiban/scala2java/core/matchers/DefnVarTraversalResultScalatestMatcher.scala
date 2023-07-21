package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.DefnVarTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnVarTraversalResultScalatestMatcher(expectedTraversalResult: DefnVarTraversalResult)
  extends Matcher[DefnVarTraversalResult] {

  override def apply(actualTraversalResult: DefnVarTraversalResult): MatchResult = {
    val matches = treeMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def treeMatches(actualTraversalResult: DefnVarTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def javaModifiersMatch(actualTraversalResult: DefnVarTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object DefnVarTraversalResultScalatestMatcher {
  def equalDefnVarTraversalResult(expectedTraversalResult: DefnVarTraversalResult): Matcher[DefnVarTraversalResult] =
    new DefnVarTraversalResultScalatestMatcher(expectedTraversalResult)
}

