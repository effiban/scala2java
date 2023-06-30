package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.TryTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Term.Block

class TryTraversalResultScalatestMatcher(expectedTraversalResult: TryTraversalResult)
  extends Matcher[TryTraversalResult] {

  override def apply(actualTraversalResult: TryTraversalResult): MatchResult = {
    val matches = exprResultMatches(actualTraversalResult) &&
    catchResultsMatch(actualTraversalResult) &&
    maybeFinallyMatches(actualTraversalResult)

    MatchResult(matches,
    s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
    s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def exprResultMatches(actualTraversalResult: TryTraversalResult) = {
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult.exprResult)(actualTraversalResult.exprResult).matches
  }

  private def catchResultsMatch(actualTraversalResult: TryTraversalResult): Boolean = {
    expectedTraversalResult.catchResults.indices.forall(idx => catchResultMatches(actualTraversalResult, idx))
  }

  private def catchResultMatches(actualTraversalResult: TryTraversalResult, idx: Int): Boolean = {
    if (idx >= actualTraversalResult.catchResults.length) {
      return false;
    }
    val expectedCatchResult = expectedTraversalResult.catchResults(idx)
    val actualCatchResult = actualTraversalResult.catchResults(idx)
    new CatchHandlerTraversalResultScalatestMatcher(expectedCatchResult)(actualCatchResult).matches
  }

  private def maybeFinallyMatches(actualTraversalResult: TryTraversalResult): Boolean = {
    new OptionMatcher[Block](expectedTraversalResult.maybeFinally, new TreeMatcher[Block](_)).matches(actualTraversalResult.maybeFinally)
  }
}

object TryTraversalResultScalatestMatcher {
  def equalTryTraversalResult(expectedTraversalResult: TryTraversalResult): Matcher[TryTraversalResult] =
    new TryTraversalResultScalatestMatcher(expectedTraversalResult)
}

