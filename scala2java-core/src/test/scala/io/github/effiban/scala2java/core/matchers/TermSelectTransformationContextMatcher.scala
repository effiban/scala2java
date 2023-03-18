package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermSelectTransformationContextMatcher(expectedContext: TermSelectTransformationContext) extends ArgumentMatcher[TermSelectTransformationContext] {

  override def matches(actualContext: TermSelectTransformationContext): Boolean = {
    maybeQualTypesMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def maybeQualTypesMatch(actualContext: TermSelectTransformationContext): Boolean = {
    new OptionMatcher(expectedContext.maybeQualType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualType)
  }
}

object TermSelectTransformationContextMatcher {
  def eqTermSelectTransformationContext(expectedContext: TermSelectTransformationContext): TermSelectTransformationContext =
    argThat(new TermSelectTransformationContextMatcher(expectedContext))
}

