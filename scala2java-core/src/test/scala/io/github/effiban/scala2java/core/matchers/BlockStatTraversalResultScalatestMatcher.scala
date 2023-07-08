package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.traversers.results._
import org.scalatest.matchers.{MatchResult, Matcher}

class BlockStatTraversalResultScalatestMatcher(expectedTraversalResult: BlockStatTraversalResult)
  extends Matcher[BlockStatTraversalResult] {

  override def apply(actualTraversalResult: BlockStatTraversalResult): MatchResult = {
    val matches = matchesByType(actualTraversalResult)

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"

  private def matchesByType(actualTraversalResult: BlockStatTraversalResult): Boolean = {
    (actualTraversalResult, expectedTraversalResult) match {
      case (actualResult: SimpleBlockStatTraversalResult, expectedResult: SimpleBlockStatTraversalResult) =>
        new SimpleBlockStatTraversalResultScalatestMatcher(expectedResult)(actualResult).matches
      case (actualResult: IfTraversalResult, expectedResult: IfTraversalResult) =>
        actualResult.stat.structure == expectedResult.stat.structure
      case (actualResult: TryTraversalResult, expectedResult: TryTraversalResult) =>
        actualResult.stat.structure == expectedResult.stat.structure
      case (actualResult: TryWithHandlerTraversalResult, expectedResult: TryWithHandlerTraversalResult) =>
        actualResult.stat.structure == expectedResult.stat.structure
      case _ => false
    }
  }
}

object BlockStatTraversalResultScalatestMatcher {
  def equalBlockStatTraversalResult(expectedTraversalResult: BlockStatTraversalResult): Matcher[BlockStatTraversalResult] =
    new BlockStatTraversalResultScalatestMatcher(expectedTraversalResult)
}

