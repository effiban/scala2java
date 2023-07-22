package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.CaseClassTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class CaseClassTraversalResultScalatestMatcher(expectedTraversalResult: CaseClassTraversalResult)
  extends Matcher[CaseClassTraversalResult] {

  override def apply(actualTraversalResult: CaseClassTraversalResult): MatchResult = {
    val matches =
      scalaModsMatch(actualTraversalResult) &&
        javaModifiersMatch(actualTraversalResult) &&
        nameMatches(actualTraversalResult) &&
        typeParamsMatch(actualTraversalResult) &&
        maybeInheritanceKeywordMatches(actualTraversalResult) &&
        initsMatch(actualTraversalResult) &&
        selfMatches(actualTraversalResult) &&
        numStatResultsMatches(actualTraversalResult) &&
        statResultsMatch(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def scalaModsMatch(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

  private def nameMatches(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.name.structure == expectedTraversalResult.name.structure
  }

  private def typeParamsMatch(actualTraversalResult: CaseClassTraversalResult): Boolean = {
    actualTraversalResult.tparams.structure == expectedTraversalResult.tparams.structure
  }

  private def maybeInheritanceKeywordMatches(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.maybeInheritanceKeyword == expectedTraversalResult.maybeInheritanceKeyword
  }

  private def initsMatch(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.inits.structure == expectedTraversalResult.inits.structure
  }

  private def selfMatches(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.self.structure == expectedTraversalResult.self.structure
  }

  private def numStatResultsMatches(actualTraversalResult: CaseClassTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: CaseClassTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object CaseClassTraversalResultScalatestMatcher {
  def equalCaseClassTraversalResult(expectedTraversalResult: CaseClassTraversalResult): Matcher[CaseClassTraversalResult] =
    new CaseClassTraversalResultScalatestMatcher(expectedTraversalResult)
}

