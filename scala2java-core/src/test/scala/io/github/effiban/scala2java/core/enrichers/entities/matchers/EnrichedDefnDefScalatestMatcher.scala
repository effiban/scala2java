package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnDef
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDefnDefScalatestMatcher(expectedEnrichedDefnDef: EnrichedDefnDef) extends Matcher[EnrichedDefnDef] {

  override def apply(actualEnrichedDefnDef: EnrichedDefnDef): MatchResult = {
    val matches = defnDefMatches(actualEnrichedDefnDef) && javaModifiersMatch(actualEnrichedDefnDef)

    MatchResult(matches,
      s"Actual enriched Defn.Def: $actualEnrichedDefnDef is NOT the same as expected enriched Defn.Def: $expectedEnrichedDefnDef",
      s"Actual enriched Defn.Def: $actualEnrichedDefnDef the same as expected enriched Defn.Def: $expectedEnrichedDefnDef"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDefnDef"

  private def defnDefMatches(actualEnrichedDefnDef: EnrichedDefnDef) = {
    actualEnrichedDefnDef.stat.structure == expectedEnrichedDefnDef.stat.structure
  }

  private def javaModifiersMatch(actualEnrichedDefnDef: EnrichedDefnDef): Boolean = {
    actualEnrichedDefnDef.javaModifiers == expectedEnrichedDefnDef.javaModifiers
  }

}

object EnrichedDefnDefScalatestMatcher {
  def equalEnrichedDefnDef(expectedEnrichedDefnDef: EnrichedDefnDef): Matcher[EnrichedDefnDef] =
    new EnrichedDefnDefScalatestMatcher(expectedEnrichedDefnDef)
}

