package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedTemplate
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedTemplateScalatestMatcher(expectedEnrichedTemplate: EnrichedTemplate) extends Matcher[EnrichedTemplate] {

  override def apply(actualEnrichedTemplate: EnrichedTemplate): MatchResult = {
    val matches =
      maybeInheritanceKeywordMatches(actualEnrichedTemplate) &&
        initsMatch(actualEnrichedTemplate) &&
        selfMatches(actualEnrichedTemplate) &&
        numStatsMatches(actualEnrichedTemplate) && 
      enrichedStatsMatch(actualEnrichedTemplate)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedTemplate is NOT the same as expected traversal result: $expectedEnrichedTemplate",
      s"Actual traversal result: $actualEnrichedTemplate the same as expected traversal result: $expectedEnrichedTemplate"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedTemplate"

  private def maybeInheritanceKeywordMatches(actualEnrichedTemplate: EnrichedTemplate) = {
    actualEnrichedTemplate.maybeInheritanceKeyword == expectedEnrichedTemplate.maybeInheritanceKeyword
  }

  private def initsMatch(actualEnrichedTemplate: EnrichedTemplate) = {
    actualEnrichedTemplate.inits.structure == expectedEnrichedTemplate.inits.structure
  }

  private def selfMatches(actualEnrichedTemplate: EnrichedTemplate) = {
    actualEnrichedTemplate.self.structure == expectedEnrichedTemplate.self.structure
  }

  private def numStatsMatches(actualEnrichedTemplate: EnrichedTemplate) = {
    actualEnrichedTemplate.enrichedStats.size == expectedEnrichedTemplate.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedTemplate: EnrichedTemplate): Boolean = {
    actualEnrichedTemplate.enrichedStats.zipWithIndex.forall { case (actualEnrichedStat, idx) =>
      val expectedStatResult = expectedEnrichedTemplate.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualEnrichedStat)(expectedStatResult).matches
    }
  }
}

object EnrichedTemplateScalatestMatcher {

  def equalEnrichedTemplate(expectedEnrichedTemplate: EnrichedTemplate): Matcher[EnrichedTemplate] =
    new EnrichedTemplateScalatestMatcher(expectedEnrichedTemplate)
}

