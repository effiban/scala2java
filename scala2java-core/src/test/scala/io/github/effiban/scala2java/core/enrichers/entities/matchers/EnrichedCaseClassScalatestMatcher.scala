package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCaseClass
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedCaseClassScalatestMatcher(expectedEnrichedCaseClass: EnrichedCaseClass) extends Matcher[EnrichedCaseClass] {

  override def apply(actualEnrichedCaseClass: EnrichedCaseClass): MatchResult = {
    val matches =
      scalaModsMatch(actualEnrichedCaseClass) &&
        javaModifiersMatch(actualEnrichedCaseClass) &&
        nameMatches(actualEnrichedCaseClass) &&
        typeParamsMatch(actualEnrichedCaseClass) &&
        ctorMatches(actualEnrichedCaseClass) &&
        maybeInheritanceKeywordMatches(actualEnrichedCaseClass) &&
        initsMatch(actualEnrichedCaseClass) &&
        selfMatches(actualEnrichedCaseClass) &&
        numStatsMatches(actualEnrichedCaseClass) &&
        enrichedStatsMatch(actualEnrichedCaseClass)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedCaseClass is NOT the same as expected traversal result: $expectedEnrichedCaseClass",
      s"Actual traversal result: $actualEnrichedCaseClass the same as expected traversal result: $expectedEnrichedCaseClass"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedCaseClass"

  private def scalaModsMatch(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.scalaMods.structure == expectedEnrichedCaseClass.scalaMods.structure
  }

  private def javaModifiersMatch(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.javaModifiers == expectedEnrichedCaseClass.javaModifiers
  }

  private def nameMatches(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.name.structure == expectedEnrichedCaseClass.name.structure
  }

  private def typeParamsMatch(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.tparams.structure == expectedEnrichedCaseClass.tparams.structure
  }

  def ctorMatches(actualEnrichedCaseClass: EnrichedCaseClass): Boolean = {
    actualEnrichedCaseClass.ctor.structure == expectedEnrichedCaseClass.ctor.structure
  }

  private def maybeInheritanceKeywordMatches(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.maybeInheritanceKeyword == expectedEnrichedCaseClass.maybeInheritanceKeyword
  }

  private def initsMatch(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.inits.structure == expectedEnrichedCaseClass.inits.structure
  }

  private def selfMatches(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.self.structure == expectedEnrichedCaseClass.self.structure
  }

  private def numStatsMatches(actualEnrichedCaseClass: EnrichedCaseClass) = {
    actualEnrichedCaseClass.enrichedStats.size == expectedEnrichedCaseClass.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedCaseClass: EnrichedCaseClass): Boolean = {
    actualEnrichedCaseClass.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedEnrichedStat = expectedEnrichedCaseClass.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedEnrichedStat).matches
    }
  }
}

object EnrichedCaseClassScalatestMatcher {
  def equalEnrichedCaseClass(expectedEnrichedCaseClass: EnrichedCaseClass): Matcher[EnrichedCaseClass] =
    new EnrichedCaseClassScalatestMatcher(expectedEnrichedCaseClass)
}

