package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.TryWithHandlerTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Term.Block

class TryWithHandlerTraversalResultScalatestMatcher(expectedTraversalResult: TryWithHandlerTraversalResult)
  extends Matcher[TryWithHandlerTraversalResult] {

  override def apply(actualTraversalResult: TryWithHandlerTraversalResult): MatchResult = {
    val matches = exprResultMatches(actualTraversalResult) &&
      catchMatches(actualTraversalResult) &&
      maybeFinallyMatches(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def exprResultMatches(actualTraversalResult: TryWithHandlerTraversalResult) = {
    new BlockTraversalResultScalatestMatcher(expectedTraversalResult.exprResult)(actualTraversalResult.exprResult).matches
  }

  private def catchMatches(actualTraversalResult: TryWithHandlerTraversalResult): Boolean = {
    expectedTraversalResult.catchp.structure == actualTraversalResult.catchp.structure
  }

  private def maybeFinallyMatches(actualTraversalResult: TryWithHandlerTraversalResult): Boolean = {
    new OptionMatcher[Block](expectedTraversalResult.maybeFinally, new TreeMatcher[Block](_)).matches(actualTraversalResult.maybeFinally)
  }
}

object TryWithHandlerTraversalResultScalatestMatcher {
  def equalTryWithHandlerTraversalResult(expectedTraversalResult: TryWithHandlerTraversalResult): Matcher[TryWithHandlerTraversalResult] =
    new TryWithHandlerTraversalResultScalatestMatcher(expectedTraversalResult)
}

