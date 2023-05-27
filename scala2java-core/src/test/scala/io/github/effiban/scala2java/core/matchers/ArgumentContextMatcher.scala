package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ArgumentContextMatcher(expectedContext: ArgumentContext) extends ArgumentMatcher[ArgumentContext] {

  override def matches(actualContext: ArgumentContext): Boolean = {
      argNameAsCommentFlagsMatch(actualContext)
  }

  private def argNameAsCommentFlagsMatch(actualContext: ArgumentContext): Boolean = actualContext.argNameAsComment == expectedContext.argNameAsComment

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArgumentContextMatcher {
  def eqArgumentContext(expectedContext: ArgumentContext): ArgumentContext =
    argThat(new ArgumentContextMatcher(expectedContext))
}

