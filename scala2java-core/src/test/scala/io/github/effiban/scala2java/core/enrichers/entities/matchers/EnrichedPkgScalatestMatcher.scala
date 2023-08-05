package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.matchers.SealedHierarchiesScalatestMatcher
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedPkgScalatestMatcher(expectedEnrichedPkg: EnrichedPkg)
  extends Matcher[EnrichedPkg] {

  override def apply(actualEnrichedPkg: EnrichedPkg): MatchResult = {
    val matches = pkgRefMatches(actualEnrichedPkg) &&
      numStatsMatch(actualEnrichedPkg) &&
      enrichedStatsMatch(actualEnrichedPkg) &&
      sealedHierarchiesMatch(actualEnrichedPkg)

    MatchResult(matches,
      s"Actual enriched Pkg: $actualEnrichedPkg is NOT the same as expected enriched Pkg: $expectedEnrichedPkg",
      s"Actual enriched Pkg: $actualEnrichedPkg the same as expected enriched Pkg: $expectedEnrichedPkg"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedPkg"

  private def pkgRefMatches(actualEnrichedPkg: EnrichedPkg) = {
    actualEnrichedPkg.pkgRef.structure == expectedEnrichedPkg.pkgRef.structure
  }

  private def numStatsMatch(actualEnrichedPkg: EnrichedPkg) = {
    actualEnrichedPkg.enrichedStats.size == expectedEnrichedPkg.enrichedStats.size
  }

  private def enrichedStatsMatch(actualEnrichedPkg: EnrichedPkg): Boolean = {
    actualEnrichedPkg.enrichedStats.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedEnrichedPkg.enrichedStats(idx)
      new EnrichedStatScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }

  private def sealedHierarchiesMatch(actualEnrichedPkg: EnrichedPkg): Boolean = {
    new SealedHierarchiesScalatestMatcher(expectedEnrichedPkg.sealedHierarchies)(actualEnrichedPkg.sealedHierarchies).matches
  }
}

object EnrichedPkgScalatestMatcher {
  def equalEnrichedPkg(expectedEnrichedPkg: EnrichedPkg): Matcher[EnrichedPkg] =
    new EnrichedPkgScalatestMatcher(expectedEnrichedPkg)
}

