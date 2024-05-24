package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Type

class UnqualifiedTermApplyTransformationContextScalatestMatcher(expectedContext: UnqualifiedTermApplyTransformationContext)
  extends Matcher[UnqualifiedTermApplyTransformationContext] {

  override def apply(actualContext: UnqualifiedTermApplyTransformationContext): MatchResult = {
    val matches = maybeParentTypeMatches(actualContext) && partialDeclDefMatches(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypeMatches(actualContext: UnqualifiedTermApplyTransformationContext) = {
    new OptionMatcher(expectedContext.maybeQualifierType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualifierType)
  }

  private def partialDeclDefMatches(actualContext: UnqualifiedTermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object UnqualifiedTermApplyTransformationContextScalatestMatcher {
  def equalUnqualifiedTermApplyTransformationContext(
    expectedContext: UnqualifiedTermApplyTransformationContext): Matcher[UnqualifiedTermApplyTransformationContext] =
    new UnqualifiedTermApplyTransformationContextScalatestMatcher(expectedContext)
}

