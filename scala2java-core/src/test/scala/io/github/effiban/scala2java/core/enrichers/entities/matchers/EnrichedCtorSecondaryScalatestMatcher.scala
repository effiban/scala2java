package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedCtorSecondary
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedCtorSecondaryScalatestMatcher(expectedEnrichedCtorSecondary: EnrichedCtorSecondary)
  extends Matcher[EnrichedCtorSecondary] {

  override def apply(actualEnrichedCtorSecondary: EnrichedCtorSecondary): MatchResult = {
    val matches = ctorSecondaryMatches(actualEnrichedCtorSecondary) &&
      classNameMatches(actualEnrichedCtorSecondary) &&
      javaModifiersMatch(actualEnrichedCtorSecondary)

    MatchResult(matches,
      s"Actual enriched ctorSecondary: $actualEnrichedCtorSecondary is NOT the same as expected enriched ctorSecondary: $expectedEnrichedCtorSecondary",
      s"Actual enriched ctorSecondary: $actualEnrichedCtorSecondary the same as expected enriched ctorSecondary: $expectedEnrichedCtorSecondary"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedCtorSecondary"

  private def ctorSecondaryMatches(actualEnrichedCtorSecondary: EnrichedCtorSecondary) = {
    actualEnrichedCtorSecondary.stat.structure == expectedEnrichedCtorSecondary.stat.structure
  }

  private def classNameMatches(actualEnrichedCtorSecondary: EnrichedCtorSecondary): Boolean = {
    actualEnrichedCtorSecondary.className.structure == expectedEnrichedCtorSecondary.className.structure
  }


  private def javaModifiersMatch(actualEnrichedCtorSecondary: EnrichedCtorSecondary): Boolean = {
    actualEnrichedCtorSecondary.javaModifiers == expectedEnrichedCtorSecondary.javaModifiers
  }
}

object EnrichedCtorSecondaryScalatestMatcher {
  def equalEnrichedCtorSecondary(expectedEnrichedCtorSecondary: EnrichedCtorSecondary): Matcher[EnrichedCtorSecondary] =
    new EnrichedCtorSecondaryScalatestMatcher(expectedEnrichedCtorSecondary)
}

