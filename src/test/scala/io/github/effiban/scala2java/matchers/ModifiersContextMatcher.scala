package io.github.effiban.scala2java.matchers

import io.github.effiban.scala2java.contexts.ModifiersContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class ModifiersContextMatcher(expectedContext: ModifiersContext) extends ArgumentMatcher[ModifiersContext] {

  override def matches(actualContext: ModifiersContext): Boolean = {
    scalaTreeMatches(actualContext) &&
      actualContext.javaTreeType == expectedContext.javaTreeType &&
      actualContext.javaScope == expectedContext.javaScope
  }

  private def scalaTreeMatches(actualContext: ModifiersContext) = {
    new TreeMatcher(expectedContext.scalaTree).matches(actualContext.scalaTree)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object ModifiersContextMatcher {
  def eqModifiersContext(expectedContext: ModifiersContext): ModifiersContext =
    argThat(new ModifiersContextMatcher(expectedContext))
}

