package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedObject
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedObjectScalatestMatcher(expectedEnrichedObject: EnrichedObject) extends Matcher[EnrichedObject] {

  override def apply(actualEnrichedObject: EnrichedObject): MatchResult = {
    val matches =
      scalaModsMatch(actualEnrichedObject) &&
        javaModifiersMatch(actualEnrichedObject) &&
        javaTypeKeywordsMatch(actualEnrichedObject) &&
        nameMatches(actualEnrichedObject) &&
        maybeInheritanceKeywordMatches(actualEnrichedObject) &&
        initsMatch(actualEnrichedObject) &&
        selfMatches(actualEnrichedObject) &&
        numStatsMatches(actualEnrichedObject) &&
        enrichedStatsMatch(actualEnrichedObject)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedObject is NOT the same as expected traversal result: $expectedEnrichedObject",
      s"Actual traversal result: $actualEnrichedObject the same as expected traversal result: $expectedEnrichedObject"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedObject"

  private def scalaModsMatch(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.scalaMods.structure == expectedEnrichedObject.scalaMods.structure
  }

  private def javaModifiersMatch(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.javaModifiers == expectedEnrichedObject.javaModifiers
  }

  private def javaTypeKeywordsMatch(actualEnrichedObject: EnrichedObject): Boolean = {
    actualEnrichedObject.javaTypeKeyword == expectedEnrichedObject.javaTypeKeyword
  }

  private def nameMatches(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.name.structure == expectedEnrichedObject.name.structure
  }

  private def maybeInheritanceKeywordMatches(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.maybeInheritanceKeyword == expectedEnrichedObject.maybeInheritanceKeyword
  }

  private def initsMatch(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.inits.structure == expectedEnrichedObject.inits.structure
  }

  private def selfMatches(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.self.structure == expectedEnrichedObject.self.structure
  }

  private def numStatsMatches(actualEnrichedObject: EnrichedObject) = {
    actualEnrichedObject.enrichedStats.size == expectedEnrichedObject.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedObject: EnrichedObject): Boolean = {
    actualEnrichedObject.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedEnrichedStat = expectedEnrichedObject.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedEnrichedStat).matches
    }
  }
}

object EnrichedObjectScalatestMatcher {
  def equalEnrichedObject(expectedEnrichedObject: EnrichedObject): Matcher[EnrichedObject] =
    new EnrichedObjectScalatestMatcher(expectedEnrichedObject)
}

