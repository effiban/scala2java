package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results._
import org.scalatest.matchers.{MatchResult, Matcher}

class DefnTraversalResultScalatestMatcher(expectedTraversalResult: DefnTraversalResult)
  extends Matcher[DefnTraversalResult] {

  override def apply(actualTraversalResult: DefnTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualDefnVarResult: DefnVarTraversalResult, expectedDefnVarResult: DefnVarTraversalResult) =>
        new DefnVarTraversalResultScalatestMatcher(expectedDefnVarResult)(actualDefnVarResult).matches
      case (actualDefnDefResult: DefnDefTraversalResult, expectedDefnDefResult: DefnDefTraversalResult) =>
        new DefnDefTraversalResultScalatestMatcher(expectedDefnDefResult)(actualDefnDefResult).matches
      case (actualResult: CtorSecondaryTraversalResult, expectedResult: CtorSecondaryTraversalResult) =>
        new CtorSecondaryTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualTraitResult: TraitTraversalResult, expectedTraitResult: TraitTraversalResult) =>
        new TraitTraversalResultScalatestMatcher(expectedTraitResult)(actualTraitResult).matches
      case (actualClassResult: ClassTraversalResult, expectedClassResult: ClassTraversalResult) =>
        new ClassTraversalResultScalatestMatcher(expectedClassResult)(actualClassResult).matches
      case (actualObjectResult: ObjectTraversalResult, expectedObjectResult: ObjectTraversalResult) =>
        new ObjectTraversalResultScalatestMatcher(expectedObjectResult)(actualObjectResult).matches
      case (actualResult: UnsupportedDefnTraversalResult, expectedResult: UnsupportedDefnTraversalResult) =>
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

object DefnTraversalResultScalatestMatcher {
  def equalDefnTraversalResult(expectedTraversalResult: DefnTraversalResult): Matcher[DefnTraversalResult] =
    new DefnTraversalResultScalatestMatcher(expectedTraversalResult)
}

