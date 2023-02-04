package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArgumentListContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Tree

class ArgumentListContextMatcher(expectedContext: ArgumentListContext) extends ArgumentMatcher[ArgumentListContext] {

  override def matches(actualContext: ArgumentListContext): Boolean = {
    maybeParentsMatch(actualContext) && optionsMatch(actualContext)
  }

  private def maybeParentsMatch(actualContext: ArgumentListContext) = {
    new OptionMatcher[Tree](expectedContext.maybeParent, new TreeMatcher[Tree](_)).matches(actualContext.maybeParent)
  }

  private def optionsMatch(actualContext: ArgumentListContext): Boolean = actualContext.options == expectedContext.options

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArgumentListContextMatcher {
  def eqArgumentListContext(expectedContext: ArgumentListContext): ArgumentListContext =
    argThat(new ArgumentListContextMatcher(expectedContext))
}

