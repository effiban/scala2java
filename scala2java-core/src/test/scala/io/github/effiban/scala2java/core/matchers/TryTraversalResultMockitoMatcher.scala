package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.TryTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Term.Block

class TryTraversalResultMockitoMatcher(expectedTraversalResult: TryTraversalResult)
  extends ArgumentMatcher[TryTraversalResult] {

  override def matches(actualTraversalResult: TryTraversalResult): Boolean = {
    exprResultMatches(actualTraversalResult) &&
      catchResultsMatch(actualTraversalResult) &&
      maybeFinallyMatches(actualTraversalResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def exprResultMatches(actualTraversalResult: TryTraversalResult) = {
    new BlockTraversalResultMockitoMatcher(expectedTraversalResult.exprResult).matches(actualTraversalResult.exprResult)
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
    new CatchHandlerTraversalResultMockitoMatcher(expectedCatchResult).matches(actualCatchResult)
  }

  private def maybeFinallyMatches(actualTraversalResult: TryTraversalResult): Boolean = {
    new OptionMatcher[Block](expectedTraversalResult.maybeFinally, new TreeMatcher[Block](_)).matches(actualTraversalResult.maybeFinally)
  }
}

object TryTraversalResultMockitoMatcher {
  def eqTryTraversalResult(expectedTraversalResult: TryTraversalResult): TryTraversalResult =
    argThat(new TryTraversalResultMockitoMatcher(expectedTraversalResult))
}

