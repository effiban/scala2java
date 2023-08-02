package io.github.effiban.scala2java.core.enrichers.entities.matchers

import io.github.effiban.scala2java.core.enrichers.entities.EnrichedDefnVar
import org.scalatest.matchers.{MatchResult, Matcher}

class EnrichedDefnVarScalatestMatcher(expectedEnrichedDefnVar: EnrichedDefnVar) extends Matcher[EnrichedDefnVar] {

  override def apply(actualEnrichedDefnVar: EnrichedDefnVar): MatchResult = {
    val matches = defnVarMatches(actualEnrichedDefnVar) && javaModifiersMatch(actualEnrichedDefnVar)

    MatchResult(matches,
      s"Actual enriched Defn.Var: $actualEnrichedDefnVar is NOT the same as expected enriched Defn.Var: $expectedEnrichedDefnVar",
      s"Actual enriched Defn.Var: $actualEnrichedDefnVar the same as expected enriched Defn.Var: $expectedEnrichedDefnVar"
    )
  }

  override def toString: String = s"Matcher for: $expectedEnrichedDefnVar"

  private def defnVarMatches(actualEnrichedDefnVar: EnrichedDefnVar) = {
    actualEnrichedDefnVar.stat.structure == expectedEnrichedDefnVar.stat.structure
  }

  private def javaModifiersMatch(actualEnrichedDefnVar: EnrichedDefnVar): Boolean = {
    actualEnrichedDefnVar.javaModifiers == expectedEnrichedDefnVar.javaModifiers
  }

}

object EnrichedDefnVarScalatestMatcher {
  def equalEnrichedDefnVar(expectedEnrichedDefnVar: EnrichedDefnVar): Matcher[EnrichedDefnVar] =
    new EnrichedDefnVarScalatestMatcher(expectedEnrichedDefnVar)
}

