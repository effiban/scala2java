package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockContextMatcher(expectedContext: BlockContext) extends ArgumentMatcher[BlockContext] {

  override def matches(actualContext: BlockContext): Boolean = {
    actualContext.shouldReturnValue == expectedContext.shouldReturnValue
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockContextMatcher {
  def eqBlockContext(expectedContext: BlockContext): BlockContext = argThat(new BlockContextMatcher(expectedContext))
}

