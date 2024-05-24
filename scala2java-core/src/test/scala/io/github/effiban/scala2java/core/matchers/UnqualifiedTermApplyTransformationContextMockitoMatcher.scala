package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class UnqualifiedTermApplyTransformationContextMockitoMatcher(expectedContext: UnqualifiedTermApplyTransformationContext)
  extends ArgumentMatcher[UnqualifiedTermApplyTransformationContext] {

  override def matches(actualContext: UnqualifiedTermApplyTransformationContext): Boolean = {
    maybeParentTypeMatches(actualContext) && partialDeclDefMatches(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypeMatches(actualContext: UnqualifiedTermApplyTransformationContext) = {
    new OptionMatcher(expectedContext.maybeQualifierType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualifierType)
  }

  private def partialDeclDefMatches(actualContext: UnqualifiedTermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object UnqualifiedTermApplyTransformationContextMockitoMatcher {
  def eqUnqualifiedTermApplyTransformationContext(
    expectedContext: UnqualifiedTermApplyTransformationContext): UnqualifiedTermApplyTransformationContext =
    argThat(new UnqualifiedTermApplyTransformationContextMockitoMatcher(expectedContext))
}

