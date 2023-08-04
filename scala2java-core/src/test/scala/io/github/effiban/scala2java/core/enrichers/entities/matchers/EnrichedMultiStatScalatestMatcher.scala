package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedMultiStat
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedMultiStatScalatestMatcher(expectedEnrichedMultiStat: EnrichedMultiStat) extends Matcher[EnrichedMultiStat] {

  override def apply(actualEnrichedMultiStat: EnrichedMultiStat): MatchResult = {
    val matches = sizeMatches(actualEnrichedMultiStat) && enrichedStatsMatch(actualEnrichedMultiStat)

    MatchResult(matches,
      s"Actual traversal result: $actualEnrichedMultiStat is NOT the same as expected traversal result: $expectedEnrichedMultiStat",
      s"Actual traversal result: $actualEnrichedMultiStat the same as expected traversal result: $expectedEnrichedMultiStat"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedMultiStat"

  private def sizeMatches(actualEnrichedMultiStat: EnrichedMultiStat) = {
    actualEnrichedMultiStat.enrichedStats.size == expectedEnrichedMultiStat.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedMultiStat: EnrichedMultiStat): Boolean = {
    actualEnrichedMultiStat.enrichedStats.zipWithIndex.forall { case (actualEnrichedStat, idx) =>
      val expectedStatResult = expectedEnrichedMultiStat.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualEnrichedStat)(expectedStatResult).matches
    }
  }
}

object EnrichedMultiStatScalatestMatcher {

  def equalEnrichedMultiStat(expectedEnrichedMultiStat: EnrichedMultiStat): Matcher[EnrichedMultiStat] =
    new EnrichedMultiStatScalatestMatcher(expectedEnrichedMultiStat)
}

