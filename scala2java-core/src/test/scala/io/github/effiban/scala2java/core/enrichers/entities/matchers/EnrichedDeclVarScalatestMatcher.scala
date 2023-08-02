package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDeclVar
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDeclVarScalatestMatcher(expectedEnrichedDeclVar: EnrichedDeclVar) extends Matcher[EnrichedDeclVar] {

  override def apply(actualEnrichedDeclVar: EnrichedDeclVar): MatchResult = {
    val matches = declVarMatches(actualEnrichedDeclVar) && javaModifiersMatch(actualEnrichedDeclVar)

    MatchResult(matches,
      s"Actual enriched Decl.Var: $actualEnrichedDeclVar is NOT the same as expected enriched Decl.Var: $expectedEnrichedDeclVar",
      s"Actual enriched Decl.Var: $actualEnrichedDeclVar the same as expected enriched Decl.Var: $expectedEnrichedDeclVar"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDeclVar"

  private def declVarMatches(actualEnrichedDeclVar: EnrichedDeclVar) = {
    actualEnrichedDeclVar.stat.structure == expectedEnrichedDeclVar.stat.structure
  }

  private def javaModifiersMatch(actualEnrichedDeclVar: EnrichedDeclVar): Boolean = {
    actualEnrichedDeclVar.javaModifiers == expectedEnrichedDeclVar.javaModifiers
  }

}

object EnrichedDeclVarScalatestMatcher {
  def equalEnrichedDeclVar(expectedEnrichedDeclVar: EnrichedDeclVar): Matcher[EnrichedDeclVar] =
    new EnrichedDeclVarScalatestMatcher(expectedEnrichedDeclVar)
}

