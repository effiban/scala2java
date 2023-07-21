package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.DeclVarTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class DeclVarTraversalResultScalatestMatcher(expectedTraversalResult: DeclVarTraversalResult)
  extends Matcher[DeclVarTraversalResult] {

  override def apply(actualTraversalResult: DeclVarTraversalResult): MatchResult = {
    val matches = treeMatches(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def treeMatches(actualTraversalResult: DeclVarTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def javaModifiersMatch(actualTraversalResult: DeclVarTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object DeclVarTraversalResultScalatestMatcher {
  def equalDeclVarTraversalResult(expectedTraversalResult: DeclVarTraversalResult): Matcher[DeclVarTraversalResult] =
    new DeclVarTraversalResultScalatestMatcher(expectedTraversalResult)
}

