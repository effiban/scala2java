package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedSource
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedSourceScalatestMatcher(expectedEnrichedSource: EnrichedSource) extends Matcher[EnrichedSource] {

  override def apply(actualEnrichedSource: EnrichedSource): MatchResult = {
    val matches = numStatsMatch(actualEnrichedSource) && statResultsMatch(actualEnrichedSource)

    MatchResult(matches,
      s"Actual enriched source: $actualEnrichedSource is NOT the same as expected enriched source: $expectedEnrichedSource",
      s"Actual enriched source: $actualEnrichedSource the same as expected enriched source: $expectedEnrichedSource"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedSource"

  private def numStatsMatch(actualEnrichedSource: EnrichedSource) = {
    actualEnrichedSource.enrichedStats.size == expectedEnrichedSource.enrichedStats.size
  }

  private def statResultsMatch(actualEnrichedSource: EnrichedSource): Boolean = {
    actualEnrichedSource.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedEnrichedSource.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object EnrichedSourceScalatestMatcher {
  def equalEnrichedSource(expectedEnrichedSource: EnrichedSource): Matcher[EnrichedSource] =
    new EnrichedSourceScalatestMatcher(expectedEnrichedSource)
}

