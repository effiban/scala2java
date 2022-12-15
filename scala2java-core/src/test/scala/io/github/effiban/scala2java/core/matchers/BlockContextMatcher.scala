package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Init

class BlockContextMatcher(expectedContext: BlockContext) extends ArgumentMatcher[BlockContext] {

  override def matches(actualContext: BlockContext): Boolean = {
    actualContext.shouldReturnValue == expectedContext.shouldReturnValue && maybeInitMatches(actualContext)
  }

  private def maybeInitMatches(actualContext: BlockContext) = {
    new OptionMatcher[Init](expectedContext.maybeInit, new TreeMatcher[Init](_)).matches(actualContext.maybeInit)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockContextMatcher {
  def eqBlockContext(expectedContext: BlockContext): BlockContext = argThat(new BlockContextMatcher(expectedContext))
}

