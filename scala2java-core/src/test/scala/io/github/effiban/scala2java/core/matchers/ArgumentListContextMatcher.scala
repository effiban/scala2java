package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ArgumentListContextMatcher(expectedContext: ArgumentListContext) extends ArgumentMatcher[ArgumentListContext] {

  override def matches(actualContext: ArgumentListContext): Boolean = {
    optionsMatch(actualContext)
  }

  private def optionsMatch(actualContext: ArgumentListContext): Boolean = actualContext.options == expectedContext.options

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArgumentListContextMatcher {
  def eqArgumentListContext(expectedContext: ArgumentListContext): ArgumentListContext =
    argThat(new ArgumentListContextMatcher(expectedContext))
}

