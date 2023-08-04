package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities._
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDefnScalatestMatcher(expectedEnrichedDefn: EnrichedDefn) extends Matcher[EnrichedDefn] {

  override def apply(actualEnrichedDefn: EnrichedDefn): MatchResult = {
    val matches = (actualEnrichedDefn, expectedEnrichedDefn) match {
      case (actualEnrichedDefn: EnrichedDefnVar, expectedEnrichedDefn: EnrichedDefnVar) =>
        new EnrichedDefnVarScalatestMatcher(expectedEnrichedDefn)(actualEnrichedDefn).matches
      case (actualEnrichedDefn: EnrichedDefnDef, expectedEnrichedDefn: EnrichedDefnDef) =>
        new EnrichedDefnDefScalatestMatcher(expectedEnrichedDefn)(actualEnrichedDefn).matches
      case (actualEnrichedDefn: EnrichedTrait, expectedEnrichedDefn: EnrichedTrait) =>
        new EnrichedTraitScalatestMatcher(expectedEnrichedDefn)(actualEnrichedDefn).matches
      // TODO handle class / object
      case (actualEnrichedDefn: EnrichedUnsupportedDefn, expectedEnrichedDefn: EnrichedUnsupportedDefn) =>
        actualEnrichedDefn.stat.structure == expectedEnrichedDefn.stat.structure
      case (anActualEnrichedDefn, anExpectedEnrichedDefn) => anActualEnrichedDefn == anExpectedEnrichedDefn
    }

    MatchResult(matches,
      s"Actual enriched Defn: $actualEnrichedDefn is NOT the same as expected enriched Defn: $expectedEnrichedDefn",
      s"Actual enriched Defn: $actualEnrichedDefn the same as expected enriched Defn: $expectedEnrichedDefn"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDefn"
}

object EnrichedDefnScalatestMatcher {
  def equalEnrichedDefn(expectedEnrichedDefn: EnrichedDefn): Matcher[EnrichedDefn] =
    new EnrichedDefnScalatestMatcher(expectedEnrichedDefn)
}

