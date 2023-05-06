package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.StatContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class StatContextMatcher(expectedContext: StatContext) extends ArgumentMatcher[StatContext] {

  override def matches(actualContext: StatContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object StatContextMatcher {
  def eqStatContext(expectedContext: StatContext): StatContext = argThat(new StatContextMatcher(expectedContext))
}

