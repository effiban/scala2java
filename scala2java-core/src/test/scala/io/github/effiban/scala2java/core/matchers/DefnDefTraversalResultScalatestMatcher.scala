package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.DefnDefTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnDefTraversalResultScalatestMatcher(expectedTraversalResult: DefnDefTraversalResult)
  extends Matcher[DefnDefTraversalResult] {

  override def apply(actualTraversalResult: DefnDefTraversalResult): MatchResult = {
    val matches = defnDefMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def defnDefMatches(actualTraversalResult: DefnDefTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def javaModifiersMatch(actualTraversalResult: DefnDefTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object DefnDefTraversalResultScalatestMatcher {
  def equalDefnDefTraversalResult(expectedTraversalResult: DefnDefTraversalResult): Matcher[DefnDefTraversalResult] =
    new DefnDefTraversalResultScalatestMatcher(expectedTraversalResult)
}

