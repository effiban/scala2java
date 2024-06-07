package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Type

class TermApplyTransformationContextScalatestMatcher(expectedContext: TermApplyTransformationContext)
  extends Matcher[TermApplyTransformationContext] {

  override def apply(actualContext: TermApplyTransformationContext): MatchResult = {
    val matches = maybeParentTypeMatches(actualContext) && partialDeclDefMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypeMatches(actualContext: TermApplyTransformationContext) = {
    new OptionMatcher(expectedContext.maybeQualifierType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualifierType)
  }

  private def partialDeclDefMatches(actualContext: TermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object TermApplyTransformationContextScalatestMatcher {
  def equalTermApplyTransformationContext(
    expectedContext: TermApplyTransformationContext): Matcher[TermApplyTransformationContext] =
    new TermApplyTransformationContextScalatestMatcher(expectedContext)
}

