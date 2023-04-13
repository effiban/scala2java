package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermApplyTransformationContextMockitoMatcher(expectedContext: TermApplyTransformationContext) extends ArgumentMatcher[TermApplyTransformationContext] {

  override def matches(actualContext: TermApplyTransformationContext): Boolean = {
    maybeParentTypeMatches(actualContext) && partialDeclDefMatches(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeParentTypeMatches(actualContext: TermApplyTransformationContext) = {
    new OptionMatcher(expectedContext.maybeParentType, new TreeMatcher[Type](_)).matches(actualContext.maybeParentType)
  }

  private def partialDeclDefMatches(actualContext: TermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object TermApplyTransformationContextMockitoMatcher {
  def eqTermApplyTransformationContext(expectedContext: TermApplyTransformationContext): TermApplyTransformationContext =
    argThat(new TermApplyTransformationContextMockitoMatcher(expectedContext))
}

