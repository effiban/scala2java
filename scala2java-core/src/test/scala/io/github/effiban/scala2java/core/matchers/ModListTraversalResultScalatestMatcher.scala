package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.ModListTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class ModListTraversalResultScalatestMatcher(expectedTraversalResult: ModListTraversalResult)
  extends Matcher[ModListTraversalResult] {

  override def apply(actualTraversalResult: ModListTraversalResult): MatchResult = {
    val matches = scalaModsMatch(actualTraversalResult) && javaModifiersMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def scalaModsMatch(actualTraversalResult: ModListTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: ModListTraversalResult): Boolean = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

}

object ModListTraversalResultScalatestMatcher {
  def equalModListTraversalResult(expectedTraversalResult: ModListTraversalResult): Matcher[ModListTraversalResult] =
    new ModListTraversalResultScalatestMatcher(expectedTraversalResult)
}

