package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.CtorSecondaryTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class CtorSecondaryTraversalResultScalatestMatcher(expectedTraversalResult: CtorSecondaryTraversalResult)
  extends Matcher[CtorSecondaryTraversalResult] {

  override def apply(actualTraversalResult: CtorSecondaryTraversalResult): MatchResult = {
    val matches = ctorSecondaryMatches(actualTraversalResult) &&
      classNameMatches(actualTraversalResult) &&
      javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def ctorSecondaryMatches(actualTraversalResult: CtorSecondaryTraversalResult) = {
    actualTraversalResult.tree.structure == expectedTraversalResult.tree.structure
  }

  private def classNameMatches(actualTraversalResult: CtorSecondaryTraversalResult): Boolean = {
    actualTraversalResult.className.structure == expectedTraversalResult.className.structure
  }


  private def javaModifiersMatch(actualTraversalResult: CtorSecondaryTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }
}

object CtorSecondaryTraversalResultScalatestMatcher {
  def equalCtorSecondaryTraversalResult(expectedTraversalResult: CtorSecondaryTraversalResult): Matcher[CtorSecondaryTraversalResult] =
    new CtorSecondaryTraversalResultScalatestMatcher(expectedTraversalResult)
}

