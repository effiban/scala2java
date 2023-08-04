package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.EnrichedPkgStatList
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesScalatestMatcher
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedPkgStatListScalatestMatcher(expectedEnrichedPkgStatList: EnrichedPkgStatList)
  extends Matcher[EnrichedPkgStatList] {

  override def apply(actualEnrichedPkgStatList: EnrichedPkgStatList): MatchResult = {
    val matches = numStatsMatch(actualEnrichedPkgStatList) &&
      enrichedStatsMatch(actualEnrichedPkgStatList) &&
      sealedHierarchiesMatch(actualEnrichedPkgStatList)

    MatchResult(matches,
      s"Actual enriched PkgStatList: $actualEnrichedPkgStatList is NOT the same as expected enriched PkgStatList: $expectedEnrichedPkgStatList",
      s"Actual enriched PkgStatList: $actualEnrichedPkgStatList the same as expected enriched PkgStatList: $expectedEnrichedPkgStatList"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedPkgStatList"

  private def numStatsMatch(actualEnrichedPkgStatList: EnrichedPkgStatList) = {
    actualEnrichedPkgStatList.enrichedStats.size == expectedEnrichedPkgStatList.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedPkgStatList: EnrichedPkgStatList): Boolean = {
    actualEnrichedPkgStatList.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedEnrichedPkgStatList.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }

  private def sealedHierarchiesMatch(actualEnrichedPkgStatList: EnrichedPkgStatList): Boolean = {
    new SealedHierarchiesScalatestMatcher(expectedEnrichedPkgStatList.sealedHierarchies)(actualEnrichedPkgStatList.sealedHierarchies).matches
  }
}

object EnrichedPkgStatListScalatestMatcher {
  def equalEnrichedPkgStatList(expectedEnrichedPkgStatList: EnrichedPkgStatList): Matcher[EnrichedPkgStatList] =
    new EnrichedPkgStatListScalatestMatcher(expectedEnrichedPkgStatList)
}

