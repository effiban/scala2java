package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.{EnrichedDecl, EnrichedDeclDef, EnrichedDeclVar, EnrichedUnsupportedDecl}
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDeclScalatestMatcher(expectedEnrichedDecl: EnrichedDecl) extends Matcher[EnrichedDecl] {

  override def apply(actualEnrichedDecl: EnrichedDecl): MatchResult = {
    val matches = (actualEnrichedDecl, expectedEnrichedDecl) match {
      case (actualEnrichedDecl: EnrichedDeclVar, expectedEnrichedDecl: EnrichedDeclVar) =>
        new EnrichedDeclVarScalatestMatcher(expectedEnrichedDecl)(actualEnrichedDecl).matches
      case (actualEnrichedDecl: EnrichedDeclDef, expectedEnrichedDecl: EnrichedDeclDef) =>
        new EnrichedDeclDefScalatestMatcher(expectedEnrichedDecl)(actualEnrichedDecl).matches
      case (actualEnrichedDecl: EnrichedUnsupportedDecl, expectedEnrichedDecl: EnrichedUnsupportedDecl) =>
        actualEnrichedDecl.stat.structure == expectedEnrichedDecl.stat.structure
      case (anActualEnrichedDecl, anExpectedEnrichedDecl) => anActualEnrichedDecl == anExpectedEnrichedDecl
    }

    MatchResult(matches,
      s"Actual enriched Decl: $actualEnrichedDecl is NOT the same as expected enriched Decl: $expectedEnrichedDecl",
      s"Actual enriched Decl: $actualEnrichedDecl the same as expected enriched Decl: $expectedEnrichedDecl"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDecl"
}

object EnrichedDeclScalatestMatcher {
  def equalEnrichedDecl(expectedEnrichedDecl: EnrichedDecl): Matcher[EnrichedDecl] =
    new EnrichedDeclScalatestMatcher(expectedEnrichedDecl)
}

