package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermSelectInferenceContextMatcher(expectedContext: TermSelectInferenceContext) extends ArgumentMatcher[TermSelectInferenceContext] {

  override def matches(actualContext: TermSelectInferenceContext): Boolean = {
    maybeQualTypesMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeQualTypesMatch(actualContext: TermSelectInferenceContext): Boolean = {
    new OptionMatcher(expectedContext.maybeQualType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualType)
  }
}

object TermSelectInferenceContextMatcher {
  def eqTermSelectInferenceContext(expectedContext: TermSelectInferenceContext): TermSelectInferenceContext =
    argThat(new TermSelectInferenceContextMatcher(expectedContext))
}

