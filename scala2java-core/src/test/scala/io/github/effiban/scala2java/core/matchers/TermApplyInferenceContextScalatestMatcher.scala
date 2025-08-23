package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.meta.Type

class TermApplyInferenceContextScalatestMatcher(expectedContext: TermApplyInferenceContext) extends Matcher[TermApplyInferenceContext] {

  override def apply(actualContext: TermApplyInferenceContext): MatchResult = {
    val matches = maybeParentTypesMatch(actualContext) && maybeArgTypesMatch(actualContext)

    MatchResult(matches,
      s"Actual context: $actualContext is NOT the same as expected context: $expectedContext",
      s"Actual context: $actualContext the same as expected context: $expectedContext"
    )

  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypesMatch(actualContext: TermApplyInferenceContext): Boolean = {
    new OptionMatcher(expectedContext.maybeParentType, new TreeMatcher[Type](_))
      .matches(actualContext.maybeParentType)
  }

  private def maybeArgTypesMatch(actualContext: TermApplyInferenceContext): Boolean = {
    val expectedMaybeArgTypeLists = expectedContext.maybeArgTypeLists
    val actualMaybeArgTypeLists = actualContext.maybeArgTypeLists

    expectedMaybeArgTypeLists.size == actualMaybeArgTypeLists.size &&
      expectedMaybeArgTypeLists.zipWithIndex.forall { case (expectedMaybeArgTypeList, index) =>
        new ListMatcher(expectedMaybeArgTypeList, new OptionMatcher[Type](_, new TreeMatcher[Type](_)))
          .matches(actualContext.maybeArgTypeLists(index))
      }
  }
}

object TermApplyInferenceContextScalatestMatcher {
  def equalTermApplyInferenceContext(expectedContext: TermApplyInferenceContext): Matcher[TermApplyInferenceContext] =
    new TermApplyInferenceContextScalatestMatcher(expectedContext)
}

