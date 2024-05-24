package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.QualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class QualifiedTermApplyTransformationContextMockitoMatcher(expectedContext: QualifiedTermApplyTransformationContext)
  extends ArgumentMatcher[QualifiedTermApplyTransformationContext] {

  override def matches(actualContext: QualifiedTermApplyTransformationContext): Boolean = {
    partialDeclDefMatches(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def partialDeclDefMatches(actualContext: QualifiedTermApplyTransformationContext) = {
    new PartialDeclDefMockitoMatcher(expectedContext.partialDeclDef).matches(actualContext.partialDeclDef)
  }
}

object QualifiedTermApplyTransformationContextMockitoMatcher {
  def eqQualifiedTermApplyTransformationContext(
    expectedContext: QualifiedTermApplyTransformationContext): QualifiedTermApplyTransformationContext =
    argThat(new QualifiedTermApplyTransformationContextMockitoMatcher(expectedContext))
}

