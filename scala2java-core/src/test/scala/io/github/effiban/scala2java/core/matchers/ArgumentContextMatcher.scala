package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

class ArgumentContextMatcher(expectedContext: ArgumentContext) extends ArgumentMatcher[ArgumentContext] {

  override def matches(actualContext: ArgumentContext): Boolean = {
    maybeParentsMatch(actualContext) && indexesMatch(actualContext)
  }

  private def maybeParentsMatch(actualContext: ArgumentContext) = {
    new OptionMatcher[Tree](expectedContext.maybeParent, new TreeMatcher[Tree](_)).matches(actualContext.maybeParent)
  }

  private def indexesMatch(actualContext: ArgumentContext): Boolean = actualContext.index == expectedContext.index

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArgumentContextMatcher {
  def eqArgumentContext(expectedContext: ArgumentContext): ArgumentContext =
    argThat(new ArgumentContextMatcher(expectedContext))
}

