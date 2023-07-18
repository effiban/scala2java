package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.DeclTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class DeclTraversalResultScalatestMatcher(expectedTraversalResult: DeclTraversalResult)
  extends Matcher[DeclTraversalResult] {

  override def apply(actualTraversalResult: DeclTraversalResult): MatchResult = {
    val matches = declMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def declMatches(actualTraversalResult: DeclTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def javaModifiersMatch(actualTraversalResult: DeclTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object DeclTraversalResultScalatestMatcher {
  def equalDeclTraversalResult(expectedTraversalResult: DeclTraversalResult): Matcher[DeclTraversalResult] =
    new DeclTraversalResultScalatestMatcher(expectedTraversalResult)
}

