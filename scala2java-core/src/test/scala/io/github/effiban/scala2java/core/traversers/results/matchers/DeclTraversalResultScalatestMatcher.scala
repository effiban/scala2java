package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.{DeclDefTraversalResult, DeclTraversalResult, DeclVarTraversalResult, UnsupportedDeclTraversalResult}
import org.scalatest.matchers.{MatchResult, Matcher}

class DeclTraversalResultScalatestMatcher(expectedTraversalResult: DeclTraversalResult)
  extends Matcher[DeclTraversalResult] {

  override def apply(actualTraversalResult: DeclTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualDeclVarResult: DeclVarTraversalResult, expectedDeclVarResult: DeclVarTraversalResult) =>
        new DeclVarTraversalResultScalatestMatcher(expectedDeclVarResult)(actualDeclVarResult).matches
      case (actualDeclDefResult: DeclDefTraversalResult, expectedDeclDefResult: DeclDefTraversalResult) =>
        new DeclDefTraversalResultScalatestMatcher(expectedDeclDefResult)(actualDeclDefResult).matches
      case (actualResult: UnsupportedDeclTraversalResult, expectedResult: UnsupportedDeclTraversalResult) =>
        actualResult.tree.structure == expectedResult.tree.structure
      case (anActualTraversalResult, anExpectedTraversalResult) => anActualTraversalResult == anExpectedTraversalResult
    }

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

object DeclTraversalResultScalatestMatcher {
  def equalDeclTraversalResult(expectedTraversalResult: DeclTraversalResult): Matcher[DeclTraversalResult] =
    new DeclTraversalResultScalatestMatcher(expectedTraversalResult)
}

