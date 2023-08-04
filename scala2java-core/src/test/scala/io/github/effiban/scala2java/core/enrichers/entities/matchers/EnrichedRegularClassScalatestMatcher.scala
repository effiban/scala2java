package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedRegularClass
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedRegularClassScalatestMatcher(expectedEnrichedRegularClass: EnrichedRegularClass) extends Matcher[EnrichedRegularClass] {

  override def apply(actualEnrichedRegularClass: EnrichedRegularClass): MatchResult = {
    val matches =
      scalaModsMatch(actualEnrichedRegularClass) &&
        javaModifiersMatch(actualEnrichedRegularClass) &&
        javaTypeKeywordsMatch(actualEnrichedRegularClass) &&
        nameMatches(actualEnrichedRegularClass) &&
        typeParamsMatch(actualEnrichedRegularClass) &&
        ctorMatches(actualEnrichedRegularClass) &&
        maybeInheritanceKeywordMatches(actualEnrichedRegularClass) &&
        initsMatch(actualEnrichedRegularClass) &&
        selfMatches(actualEnrichedRegularClass) &&
        numStatsMatches(actualEnrichedRegularClass) &&
        enrichedStatsMatch(actualEnrichedRegularClass)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedRegularClass is NOT the same as expected traversal result: $expectedEnrichedRegularClass",
      s"Actual traversal result: $actualEnrichedRegularClass the same as expected traversal result: $expectedEnrichedRegularClass"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedRegularClass"

  private def scalaModsMatch(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.scalaMods.structure == expectedEnrichedRegularClass.scalaMods.structure
  }

  private def javaModifiersMatch(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.javaModifiers == expectedEnrichedRegularClass.javaModifiers
  }

  private def javaTypeKeywordsMatch(actualEnrichedRegularClass: EnrichedRegularClass): Boolean = {
    actualEnrichedRegularClass.javaTypeKeyword == expectedEnrichedRegularClass.javaTypeKeyword
  }

  private def nameMatches(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.name.structure == expectedEnrichedRegularClass.name.structure
  }

  private def typeParamsMatch(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.tparams.structure == expectedEnrichedRegularClass.tparams.structure
  }

  def ctorMatches(actualEnrichedRegularClass: EnrichedRegularClass): Boolean = {
    actualEnrichedRegularClass.ctor.structure == expectedEnrichedRegularClass.ctor.structure
  }

  private def maybeInheritanceKeywordMatches(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.maybeInheritanceKeyword == expectedEnrichedRegularClass.maybeInheritanceKeyword
  }

  private def initsMatch(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.inits.structure == expectedEnrichedRegularClass.inits.structure
  }

  private def selfMatches(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.self.structure == expectedEnrichedRegularClass.self.structure
  }

  private def numStatsMatches(actualEnrichedRegularClass: EnrichedRegularClass) = {
    actualEnrichedRegularClass.enrichedStats.size == expectedEnrichedRegularClass.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedRegularClass: EnrichedRegularClass): Boolean = {
    actualEnrichedRegularClass.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedEnrichedStat = expectedEnrichedRegularClass.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedEnrichedStat).matches
    }
  }
}

object EnrichedRegularClassScalatestMatcher {
  def equalEnrichedRegularClass(expectedEnrichedRegularClass: EnrichedRegularClass): Matcher[EnrichedRegularClass] =
    new EnrichedRegularClassScalatestMatcher(expectedEnrichedRegularClass)
}

