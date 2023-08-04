package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedCtorSecondary, EnrichedDecl, EnrichedDefn, EnrichedStatWithJavaModifiers}
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedStatWithJavaModifiersScalatestMatcher(expectedEnriched: EnrichedStatWithJavaModifiers)
  extends Matcher[EnrichedStatWithJavaModifiers] {

  override def apply(actualEnriched: EnrichedStatWithJavaModifiers): MatchResult = {
    val matches = (actualEnriched, expectedEnriched) match {
      case (actualEnriched: EnrichedDecl, expectedEnriched: EnrichedDecl) =>
        new EnrichedDeclScalatestMatcher(expectedEnriched)(actualEnriched).matches
      case (actualEnriched: EnrichedDefn, expectedEnriched: EnrichedDefn) =>
        new EnrichedDefnScalatestMatcher(expectedEnriched)(actualEnriched).matches
      case (actualEnriched: EnrichedCtorSecondary, expectedEnriched: EnrichedCtorSecondary) =>
        new EnrichedCtorSecondaryScalatestMatcher(expectedEnriched)(actualEnriched).matches
      case (anActualEnriched, anExpectedEnriched) => anActualEnriched == anExpectedEnriched
    }

    MatchResult(matches,
      s"Actual enriched: $actualEnriched is NOT the same as expected enriched: $expectedEnriched",
      s"Actual enriched: $actualEnriched the same as expected enriched: $expectedEnriched"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnriched"
}

object EnrichedStatWithJavaModifiersScalatestMatcher {
  def equalEnrichedStatWithJavaModifiers(expectedEnriched: EnrichedStatWithJavaModifiers) : Matcher[EnrichedStatWithJavaModifiers] =
    new EnrichedStatWithJavaModifiersScalatestMatcher(expectedEnriched)
}

