package io.github.effiban.scala2java.test.utils.matchers

import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply
import org.scalatest.matchers.{MatchResult, Matcher}

class UnqualifiedTermApplyScalatestMatcher(expectedUnqualifiedTermApply: UnqualifiedTermApply)
  extends Matcher[UnqualifiedTermApply] {

  override def apply(actualUnqualifiedTermApply: UnqualifiedTermApply): MatchResult = {
    val matches = nameMatches(actualUnqualifiedTermApply) &&
      typeArgsMatch(actualUnqualifiedTermApply) &&
      argsMatch(actualUnqualifiedTermApply)

    MatchResult(matches,
      s"Actual unqualifiedTermApply: $actualUnqualifiedTermApply is NOT the same as expected unqualifiedTermApply: $expectedUnqualifiedTermApply",
      s"Actual unqualifiedTermApply: $actualUnqualifiedTermApply the same as expected unqualifiedTermApply: $expectedUnqualifiedTermApply"
    )
  }

  override def toString: String = s"Matcher for: $expectedUnqualifiedTermApply"

  private def nameMatches(actualUnqualifiedTermApply: UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.name.structure == expectedUnqualifiedTermApply.name.structure
  }

  private def typeArgsMatch(actualUnqualifiedTermApply : UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.typeArgs.structure == expectedUnqualifiedTermApply.typeArgs.structure
  }

  private def argsMatch(actualUnqualifiedTermApply : UnqualifiedTermApply) = {
    actualUnqualifiedTermApply.args.structure == expectedUnqualifiedTermApply.args.structure
  }
}

object UnqualifiedTermApplyScalatestMatcher {
  def equalUnqualifiedTermApply(expectedUnqualifiedTermApply: UnqualifiedTermApply): Matcher[UnqualifiedTermApply] =
    new UnqualifiedTermApplyScalatestMatcher(expectedUnqualifiedTermApply)
}

