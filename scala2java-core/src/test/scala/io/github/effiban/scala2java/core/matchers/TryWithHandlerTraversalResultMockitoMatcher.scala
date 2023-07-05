package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results.TryWithHandlerTraversalResult
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Term.Block

class TryWithHandlerTraversalResultMockitoMatcher(expectedTraversalResult: TryWithHandlerTraversalResult)
  extends ArgumentMatcher[TryWithHandlerTraversalResult] {

  override def matches(actualTraversalResult: TryWithHandlerTraversalResult): Boolean = {
    exprResultMatches(actualTraversalResult) &&
      catchMatches(actualTraversalResult) &&
      maybeFinallyMatches(actualTraversalResult)
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def exprResultMatches(actualTraversalResult: TryWithHandlerTraversalResult) = {
    new BlockTraversalResultMockitoMatcher(expectedTraversalResult.exprResult).matches(actualTraversalResult.exprResult)
  }

  private def catchMatches(actualTraversalResult: TryWithHandlerTraversalResult): Boolean = {
    expectedTraversalResult.catchp.structure == actualTraversalResult.catchp.structure
  }

  private def maybeFinallyMatches(actualTraversalResult: TryWithHandlerTraversalResult): Boolean = {
    new OptionMatcher[Block](expectedTraversalResult.maybeFinally, new TreeMatcher[Block](_)).matches(actualTraversalResult.maybeFinally)
  }
}

object TryWithHandlerTraversalResultMockitoMatcher {
  def eqTryWithHandlerTraversalResult(expectedTraversalResult: TryWithHandlerTraversalResult): TryWithHandlerTraversalResult =
    argThat(new TryWithHandlerTraversalResultMockitoMatcher(expectedTraversalResult))
}

