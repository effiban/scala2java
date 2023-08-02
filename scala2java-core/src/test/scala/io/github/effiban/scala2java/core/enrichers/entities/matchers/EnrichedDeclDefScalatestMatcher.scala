package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclDef
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDeclDefScalatestMatcher(expectedEnrichedDeclDef: EnrichedDeclDef) extends Matcher[EnrichedDeclDef] {

  override def apply(actualEnrichedDeclDef: EnrichedDeclDef): MatchResult = {
    val matches = declDefMatches(actualEnrichedDeclDef) && javaModifiersMatch(actualEnrichedDeclDef)

    MatchResult(matches,
      s"Actual enriched Decl.Def: $actualEnrichedDeclDef is NOT the same as expected enriched Decl.Def: $expectedEnrichedDeclDef",
      s"Actual enriched Decl.Def: $actualEnrichedDeclDef the same as expected enriched Decl.Def: $expectedEnrichedDeclDef"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDeclDef"

  private def declDefMatches(actualEnrichedDeclDef: EnrichedDeclDef) = {
    actualEnrichedDeclDef.stat.structure == expectedEnrichedDeclDef.stat.structure
  }

  private def javaModifiersMatch(actualEnrichedDeclDef: EnrichedDeclDef): Boolean = {
    actualEnrichedDeclDef.javaModifiers == expectedEnrichedDeclDef.javaModifiers
  }

}

object EnrichedDeclDefScalatestMatcher {
  def equalEnrichedDeclDef(expectedEnrichedDeclDef: EnrichedDeclDef): Matcher[EnrichedDeclDef] =
    new EnrichedDeclDefScalatestMatcher(expectedEnrichedDeclDef)
}

