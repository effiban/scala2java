package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.QualifiedTermApplyTransformationContext
import org.scalatest.matchers.{MatchResult, Matcher}

class QualifiedTermApplyTransformationContextScalatestMatcher(expectedContext: QualifiedTermApplyTransformationContext)
  extends Matcher[QualifiedTermApplyTransformationContext] {

  override def apply(actualContext: QualifiedTermApplyTransformationContext): MatchResult = {
    val matches = partialDeclDefMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def partialDeclDefMatches(actualContext: QualifiedTermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object QualifiedTermApplyTransformationContextScalatestMatcher {
  def equalQualifiedTermApplyTransformationContext(
    expectedContext: QualifiedTermApplyTransformationContext): Matcher[QualifiedTermApplyTransformationContext] =
    new QualifiedTermApplyTransformationContextScalatestMatcher(expectedContext)
}

