package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.DefnDefContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class DefnDefContextMatcher(expectedContext: DefnDefContext) extends ArgumentMatcher[DefnDefContext] {

  override def matches(actualContext: DefnDefContext): Boolean = {
    actualContext.javaScope == expectedContext.javaScope
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object DefnDefContextMatcher {
  def eqDefnDefContext(expectedContext: DefnDefContext): DefnDefContext = argThat(new DefnDefContextMatcher(expectedContext))
}

