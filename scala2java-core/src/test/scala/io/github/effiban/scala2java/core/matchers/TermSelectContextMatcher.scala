package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.test.utils.matchers.{ListMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Type

class TermSelectContextMatcher(expectedContext: TermSelectContext) extends ArgumentMatcher[TermSelectContext] {

  override def matches(actualContext: TermSelectContext): Boolean = {
    new ListMatcher(expectedContext.appliedTypeArgs, new TreeMatcher[Type](_)).matches(actualContext.appliedTypeArgs)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object TermSelectContextMatcher {
  def eqTermSelectContext(expectedContext: TermSelectContext): TermSelectContext = argThat(new TermSelectContextMatcher(expectedContext))
}

