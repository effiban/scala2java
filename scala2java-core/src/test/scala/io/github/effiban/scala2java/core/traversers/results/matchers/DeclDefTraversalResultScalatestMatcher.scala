package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.DeclDefTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class DeclDefTraversalResultScalatestMatcher(expectedTraversalResult: DeclDefTraversalResult)
  extends Matcher[DeclDefTraversalResult] {

  override def apply(actualTraversalResult: DeclDefTraversalResult): MatchResult = {
    val matches = declDefMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def declDefMatches(actualTraversalResult: DeclDefTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def javaModifiersMatch(actualTraversalResult: DeclDefTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object DeclDefTraversalResultScalatestMatcher {
  def equalDeclDefTraversalResult(expectedTraversalResult: DeclDefTraversalResult): Matcher[DeclDefTraversalResult] =
    new DeclDefTraversalResultScalatestMatcher(expectedTraversalResult)
}

