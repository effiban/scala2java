package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities._
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedClassScalatestMatcher(expectedEnrichedClass: EnrichedClass) extends Matcher[EnrichedClass] {

  override def apply(actualEnrichedClass: EnrichedClass): MatchResult = {
    val matches = (actualEnrichedClass, expectedEnrichedClass) match {
      case (actualEnrichedClass: EnrichedCaseClass, expectedEnrichedClass: EnrichedCaseClass) =>
        new EnrichedCaseClassScalatestMatcher(expectedEnrichedClass)(actualEnrichedClass).matches
      case (actualEnrichedClass: EnrichedRegularClass, expectedEnrichedClass: EnrichedRegularClass) =>
        new EnrichedRegularClassScalatestMatcher(expectedEnrichedClass)(actualEnrichedClass).matches
      case (anActualEnrichedClass, anExpectedEnrichedClass) => anActualEnrichedClass == anExpectedEnrichedClass
    }

    MatchResult(matches,
      s"Actual enriched class: $actualEnrichedClass is NOT the same as expected enriched class: $expectedEnrichedClass",
      s"Actual enriched class: $actualEnrichedClass the same as expected enriched class: $expectedEnrichedClass"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedClass"
}

object EnrichedClassScalatestMatcher {
  def equalEnrichedClass(expectedEnrichedClass: EnrichedClass): Matcher[EnrichedClass] =
    new EnrichedClassScalatestMatcher(expectedEnrichedClass)
}

