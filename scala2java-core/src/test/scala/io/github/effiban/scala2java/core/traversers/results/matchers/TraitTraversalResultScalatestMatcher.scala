package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.TraitTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class TraitTraversalResultScalatestMatcher(expectedTraversalResult: TraitTraversalResult)
  extends Matcher[TraitTraversalResult] {

  override def apply(actualTraversalResult: TraitTraversalResult): MatchResult = {
    val matches =
      scalaModsMatch(actualTraversalResult) &&
        javaModifiersMatch(actualTraversalResult) &&
        nameMatches(actualTraversalResult) &&
        typeParamsMatch(actualTraversalResult) &&
        initsMatch(actualTraversalResult) &&
        selfMatches(actualTraversalResult) &&
        numStatResultsMatches(actualTraversalResult) &&
        statResultsMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def scalaModsMatch(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

  private def nameMatches(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.name.structure == expectedTraversalResult.name.structure
  }

  private def typeParamsMatch(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.tparams.structure == expectedTraversalResult.tparams.structure
  }

  private def initsMatch(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.inits.structure == expectedTraversalResult.inits.structure
  }

  private def selfMatches(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.self.structure == expectedTraversalResult.self.structure
  }

  private def numStatResultsMatches(actualTraversalResult: TraitTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: TraitTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object TraitTraversalResultScalatestMatcher {
  def equalTraitTraversalResult(expectedTraversalResult: TraitTraversalResult): Matcher[TraitTraversalResult] =
    new TraitTraversalResultScalatestMatcher(expectedTraversalResult)
}
