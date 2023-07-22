package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.RegularClassTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class RegularClassTraversalResultScalatestMatcher(expectedTraversalResult: RegularClassTraversalResult)
  extends Matcher[RegularClassTraversalResult] {

  override def apply(actualTraversalResult: RegularClassTraversalResult): MatchResult = {
    val matches =
      scalaModsMatch(actualTraversalResult) &&
        javaModifiersMatch(actualTraversalResult) &&
        javaTypeKeywordsMatch(actualTraversalResult) &&
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

  private def scalaModsMatch(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

  private def javaTypeKeywordsMatch(actualTraversalResult: RegularClassTraversalResult): Boolean = {
    actualTraversalResult.javaTypeKeyword == expectedTraversalResult.javaTypeKeyword
  }

  private def nameMatches(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.name.structure == expectedTraversalResult.name.structure
  }

  private def typeParamsMatch(actualTraversalResult: RegularClassTraversalResult): Boolean = {
    actualTraversalResult.tparams.structure == expectedTraversalResult.tparams.structure
  }

  private def maybeInheritanceKeywordMatches(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.maybeInheritanceKeyword == expectedTraversalResult.maybeInheritanceKeyword
  }

  private def initsMatch(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.inits.structure == expectedTraversalResult.inits.structure
  }

  private def selfMatches(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.self.structure == expectedTraversalResult.self.structure
  }

  private def numStatResultsMatches(actualTraversalResult: RegularClassTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: RegularClassTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object RegularClassTraversalResultScalatestMatcher {
  def equalRegularClassTraversalResult(expectedTraversalResult: RegularClassTraversalResult): Matcher[RegularClassTraversalResult] =
    new RegularClassTraversalResultScalatestMatcher(expectedTraversalResult)
}

