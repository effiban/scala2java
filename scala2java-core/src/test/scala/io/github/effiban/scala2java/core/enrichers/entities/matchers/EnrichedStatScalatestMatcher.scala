package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.EnrichedPkg
import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedSimpleStat, EnrichedStat, EnrichedStatWithJavaModifiers}
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedStatScalatestMatcher(expectedEnriched: EnrichedStat) extends Matcher[EnrichedStat] {

  override def apply(actualEnriched: EnrichedStat): MatchResult = {
    val matches = (actualEnriched, expectedEnriched) match {
      case (actualEnriched: EnrichedPkg, expectedEnriched: EnrichedPkg) =>
        new EnrichedPkgScalatestMatcher(expectedEnriched)(actualEnriched).matches
      case (actualEnriched: EnrichedStatWithJavaModifiers, expectedEnriched: EnrichedStatWithJavaModifiers) =>
        new EnrichedStatWithJavaModifiersScalatestMatcher(expectedEnriched)(actualEnriched).matches
      case (actualEnriched: EnrichedSimpleStat, expectedEnriched: EnrichedSimpleStat) =>
        actualEnriched.stat.structure == expectedEnriched.stat.structure
      case (anActualEnriched, anExpectedEnriched) => anActualEnriched == anExpectedEnriched
    }

    MatchResult(matches,
      s"Actual enriched: $actualEnriched is NOT the same as expected enriched: $expectedEnriched",
      s"Actual enriched: $actualEnriched the same as expected enriched: $expectedEnriched"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnriched"
}

object EnrichedStatScalatestMatcher {
  def equalEnrichedStat(expectedEnriched: EnrichedStat) : Matcher[EnrichedStat] =
    new EnrichedStatScalatestMatcher(expectedEnriched)
}

