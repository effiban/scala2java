package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermApplyInferenceContextMockitoMatcher(expectedContext: TermApplyInferenceContext) extends ArgumentMatcher[TermApplyInferenceContext] {

  override def matches(actualContext: TermApplyInferenceContext): Boolean = {
    maybeParentTypesMatch(actualContext) && maybeArgTypesMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypesMatch(actualContext: TermApplyInferenceContext): Boolean = {
    new OptionMatcher(expectedContext.maybeParentType, new TreeMatcher[Type](_)).matches(actualContext.maybeParentType)
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

object TermApplyInferenceContextMockitoMatcher {
  def eqTermApplyInferenceContext(expectedSignature: TermApplyInferenceContext): TermApplyInferenceContext =
    argThat(new TermApplyInferenceContextMockitoMatcher(expectedSignature))
}

