package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedModList
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedModListScalatestMatcher(expectedEnrichedModList: EnrichedModList)
  extends Matcher[EnrichedModList] {

  override def apply(actualEnrichedModList: EnrichedModList): MatchResult = {
    val matches = scalaModsMatch(actualEnrichedModList) && javaModifiersMatch(actualEnrichedModList)

    MatchResult(matches,
      s"Actual enriched mod list: $actualEnrichedModList is NOT the same as expected enriched mod list: $expectedEnrichedModList",
      s"Actual enriched mod list: $actualEnrichedModList the same as expected enriched mod list: $expectedEnrichedModList"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedModList"

  private def scalaModsMatch(actualEnrichedModList: EnrichedModList) = {
    actualEnrichedModList.scalaMods.structure == expectedEnrichedModList.scalaMods.structure
  }

  private def javaModifiersMatch(actualEnrichedModList: EnrichedModList): Boolean = {
    actualEnrichedModList.javaModifiers == expectedEnrichedModList.javaModifiers
  }

}

object EnrichedModListScalatestMatcher {
  def equalEnrichedModList(expectedEnrichedModList: EnrichedModList): Matcher[EnrichedModList] =
    new EnrichedModListScalatestMatcher(expectedEnrichedModList)
}

