package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.TemplateTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Init

class TemplateTraversalResultScalatestMatcher(expectedTraversalResult: TemplateTraversalResult)
  extends Matcher[TemplateTraversalResult] {

  override def apply(actualTraversalResult: TemplateTraversalResult): MatchResult = {
    val matches =
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

  private def numStatResultsMatches(actualTraversalResult: TemplateTraversalResult) = {
    actualTraversalResult.statResults.size == expectedTraversalResult.statResults.size
  }

  private def maybeInheritanceKeywordMatches(actualTraversalResult: TemplateTraversalResult) = {
    actualTraversalResult.maybeInheritanceKeyword == expectedTraversalResult.maybeInheritanceKeyword
  }

  private def initsMatch(actualTraversalResult: TemplateTraversalResult) = {
    new ListMatcher(expectedTraversalResult.inits, new TreeMatcher[Init](_)).matches(actualTraversalResult.inits)
  }

  private def selfMatches(actualTraversalResult: TemplateTraversalResult) = {
    actualTraversalResult.self.structure == expectedTraversalResult.self.structure
  }

  private def statResultsMatch(actualTraversalResult: TemplateTraversalResult): Boolean = {
    actualTraversalResult.statResults.zipWithIndex.forall { case (actualStatResult, idx) =>
      val expectedStatResult = expectedTraversalResult.statResults(idx)
      new StatTraversalResultScalatestMatcher(actualStatResult)(expectedStatResult).matches
    }
  }
}

object TemplateTraversalResultScalatestMatcher {
  def equalTemplateTraversalResult(expectedTraversalResult: TemplateTraversalResult): Matcher[TemplateTraversalResult] =
    new TemplateTraversalResultScalatestMatcher(expectedTraversalResult)
}

