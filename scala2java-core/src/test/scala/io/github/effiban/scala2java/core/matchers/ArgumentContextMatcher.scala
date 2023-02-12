package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.{Term, Tree}

class ArgumentContextMatcher(expectedContext: ArgumentContext) extends ArgumentMatcher[ArgumentContext] {

  override def matches(actualContext: ArgumentContext): Boolean = {
    maybeParentsMatch(actualContext) &&
      maybeNamesMatch(actualContext) &&
      indexesMatch(actualContext) &&
      argNameAsCommentFlagsMatch(actualContext)
  }

  private def maybeParentsMatch(actualContext: ArgumentContext) = {
    new OptionMatcher[Tree](expectedContext.maybeParent, new TreeMatcher[Tree](_)).matches(actualContext.maybeParent)
  }

  private def maybeNamesMatch(actualContext: ArgumentContext) = {
    new OptionMatcher[Term.Name](expectedContext.maybeName, new TreeMatcher[Term.Name](_)).matches(actualContext.maybeName)
  }

  private def indexesMatch(actualContext: ArgumentContext): Boolean = actualContext.index == expectedContext.index

  private def argNameAsCommentFlagsMatch(actualContext: ArgumentContext): Boolean = actualContext.argNameAsComment == expectedContext.argNameAsComment

  override def toString: String = s"Matcher for: $expectedContext"
}

object ArgumentContextMatcher {
  def eqArgumentContext(expectedContext: ArgumentContext): ArgumentContext =
    argThat(new ArgumentContextMatcher(expectedContext))
}

