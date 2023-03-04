package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.spi.contexts.TermSelectContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermSelectContextMatcher(expectedContext: TermSelectContext) extends ArgumentMatcher[TermSelectContext] {

  override def matches(actualContext: TermSelectContext): Boolean = {
    appliedTypeArgsMatch(actualContext) && maybeQualTypesMatch(actualContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"

  private def appliedTypeArgsMatch(actualContext: TermSelectContext) = {
    new ListMatcher(expectedContext.appliedTypeArgs, new TreeMatcher[Type](_)).matches(actualContext.appliedTypeArgs)
  }

  private def maybeQualTypesMatch(actualContext: TermSelectContext): Boolean = {
    new OptionMatcher(expectedContext.maybeQualType, new TreeMatcher[Type](_)).matches(actualContext.maybeQualType)
  }
}

object TermSelectContextMatcher {
  def eqTermSelectContext(expectedContext: TermSelectContext): TermSelectContext = argThat(new TermSelectContextMatcher(expectedContext))
}

