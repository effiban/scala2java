package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.ObjectTraversalResult
import org.scalatest.matchers.{MatchResult, Matcher}

class ObjectTraversalResultScalatestMatcher(expectedTraversalResult: ObjectTraversalResult)
  extends Matcher[ObjectTraversalResult] {

  override def apply(actualTraversalResult: ObjectTraversalResult): MatchResult = {
    val matches =
      scalaModsMatch(actualTraversalResult) &&
        javaModifiersMatch(actualTraversalResult) &&
        javaTypeKeywordMatches(actualTraversalResult) &&
        nameMatches(actualTraversalResult) &&
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

  private def scalaModsMatch(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.scalaMods.structure == expectedTraversalResult.scalaMods.structure
  }

  private def javaModifiersMatch(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.javaModifiers == expectedTraversalResult.javaModifiers
  }

  private def javaTypeKeywordMatches(actualTraversalResult: ObjectTraversalResult): Boolean = {
    actualTraversalResult.javaTypeKeyword == expectedTraversalResult.javaTypeKeyword
  }

  private def nameMatches(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.name.structure == expectedTraversalResult.name.structure
  }

  private def maybeInheritanceKeywordMatches(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.maybeInheritanceKeyword == expectedTraversalResult.maybeInheritanceKeyword
  }

  private def initsMatch(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.inits.structure == expectedTraversalResult.inits.structure
  }

  private def selfMatches(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.self.structure == expectedTraversalResult.self.structure
  }

  private def numStatResultsMatches(actualTraversalResult: ObjectTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def statResultsMatch(actualTraversalResult: ObjectTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object ObjectTraversalResultScalatestMatcher {
  def equalObjectTraversalResult(expectedTraversalResult: ObjectTraversalResult): Matcher[ObjectTraversalResult] =
    new ObjectTraversalResultScalatestMatcher(expectedTraversalResult)
}

