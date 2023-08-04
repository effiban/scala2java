package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTrait
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedTraitScalatestMatcher(expectedEnrichedTrait: EnrichedTrait) extends Matcher[EnrichedTrait] {

  override def apply(actualEnrichedTrait: EnrichedTrait): MatchResult = {
    val matches =
      scalaModsMatch(actualEnrichedTrait) &&
        javaModifiersMatch(actualEnrichedTrait) &&
        nameMatches(actualEnrichedTrait) &&
        typeParamsMatch(actualEnrichedTrait) &&
        initsMatch(actualEnrichedTrait) &&
        selfMatches(actualEnrichedTrait) &&
        numStatsMatches(actualEnrichedTrait) &&
        enrichedStatsMatch(actualEnrichedTrait)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedTrait is NOT the same as expected traversal result: $expectedEnrichedTrait",
      s"Actual traversal result: $actualEnrichedTrait the same as expected traversal result: $expectedEnrichedTrait"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedTrait"

  private def scalaModsMatch(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.scalaMods.structure == expectedEnrichedTrait.scalaMods.structure
  }

  private def javaModifiersMatch(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.javaModifiers == expectedEnrichedTrait.javaModifiers
  }

  private def nameMatches(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.name.structure == expectedEnrichedTrait.name.structure
  }

  private def typeParamsMatch(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.tparams.structure == expectedEnrichedTrait.tparams.structure
  }

  private def initsMatch(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.inits.structure == expectedEnrichedTrait.inits.structure
  }

  private def selfMatches(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.self.structure == expectedEnrichedTrait.self.structure
  }

  private def numStatsMatches(actualEnrichedTrait: EnrichedTrait) = {
    actualEnrichedTrait.enrichedStats.size == expectedEnrichedTrait.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedTrait: EnrichedTrait): Boolean = {
    actualEnrichedTrait.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedEnrichedStat = expectedEnrichedTrait.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedEnrichedStat).matches
    }
  }
}

object EnrichedTraitScalatestMatcher {
  def equalEnrichedTrait(expectedEnrichedTrait: EnrichedTrait): Matcher[EnrichedTrait] =
    new EnrichedTraitScalatestMatcher(expectedEnrichedTrait)
}

