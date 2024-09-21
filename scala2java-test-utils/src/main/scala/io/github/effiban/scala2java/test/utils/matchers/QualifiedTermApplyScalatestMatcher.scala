package io.github.effiban.scala2java.test.utils.matchers

import io.github.effiban.scala2java.spi.entities.QualifiedTermApply
import org.scalatest.matchers.{MatchResult, Matcher}

class QualifiedTermApplyScalatestMatcher(expectedQualifiedTermApply: QualifiedTermApply)
  extends Matcher[QualifiedTermApply] {

  override def apply(actualQualifiedTermApply: QualifiedTermApply): MatchResult = {
    val matches = qualifiedNameMatches(actualQualifiedTermApply) &&
      typeArgsMatch(actualQualifiedTermApply) &&
      argsMatch(actualQualifiedTermApply)

    MatchResult(matches,
      s"Actual qualifiedTermApply: $actualQualifiedTermApply is NOT the same as expected qualifiedTermApply: $expectedQualifiedTermApply",
      s"Actual qualifiedTermApply: $actualQualifiedTermApply the same as expected qualifiedTermApply: $expectedQualifiedTermApply"
    )
  }

  override def toString: String = s"Matcher for: $expectedQualifiedTermApply"

  private def qualifiedNameMatches(actualQualifiedTermApply: QualifiedTermApply) = {
    actualQualifiedTermApply.qualifiedName.structure == expectedQualifiedTermApply.qualifiedName.structure
  }

  private def typeArgsMatch(actualQualifiedTermApply : QualifiedTermApply) = {
    actualQualifiedTermApply.typeArgs.structure == expectedQualifiedTermApply.typeArgs.structure
  }

  private def argsMatch(actualQualifiedTermApply : QualifiedTermApply) = {
    actualQualifiedTermApply.args.structure == expectedQualifiedTermApply.args.structure
  }
}

object QualifiedTermApplyScalatestMatcher {
  def equalQualifiedTermApply(expectedQualifiedTermApply: QualifiedTermApply): Matcher[QualifiedTermApply] =
    new QualifiedTermApplyScalatestMatcher(expectedQualifiedTermApply)
}

