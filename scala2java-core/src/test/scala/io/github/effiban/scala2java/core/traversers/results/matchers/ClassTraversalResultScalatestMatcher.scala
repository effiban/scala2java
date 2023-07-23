package io.github.effiban.scala2java.core.traversers.results.matchers

import io.github.effiban.scala2java.core.traversers.results.{CaseClassTraversalResult, ClassTraversalResult, RegularClassTraversalResult}
import org.scalatest.matchers.{MatchResult, Matcher}

class ClassTraversalResultScalatestMatcher(expectedTraversalResult: ClassTraversalResult)
  extends Matcher[ClassTraversalResult] {

  override def apply(actualTraversalResult: ClassTraversalResult): MatchResult = {
    val matches = (actualTraversalResult, expectedTraversalResult) match {
      case (actualCaseClassResult: CaseClassTraversalResult, expectedCaseClassResult: CaseClassTraversalResult) =>
        new CaseClassTraversalResultScalatestMatcher(expectedCaseClassResult)(actualCaseClassResult).matches
      case (actualRegularClassResult: RegularClassTraversalResult, expectedRegularClassResult: RegularClassTraversalResult) =>
        new RegularClassTraversalResultScalatestMatcher(expectedRegularClassResult)(actualRegularClassResult).matches
      case (anActualTraversalResult, anExpectedTraversalResult) => anActualTraversalResult == anExpectedTraversalResult
    }

    MatchResult(matches,
      s"Actual traversal result: $actualTraversalResult is NOT the same as expected traversal result: $expectedTraversalResult",
      s"Actual traversal result: $actualTraversalResult the same as expected traversal result: $expectedTraversalResult"
    )
  }

  override def toString: String = s"Matcher for: $expectedTraversalResult"
}

object ClassTraversalResultScalatestMatcher {
  def equalClassTraversalResult(expectedTraversalResult: ClassTraversalResult): Matcher[ClassTraversalResult] =
    new ClassTraversalResultScalatestMatcher(expectedTraversalResult)
}

