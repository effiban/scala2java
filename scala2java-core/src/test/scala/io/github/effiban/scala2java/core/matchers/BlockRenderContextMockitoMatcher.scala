package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

class BlockRenderContextMockitoMatcher(expectedContext: BlockRenderContext) extends ArgumentMatcher[BlockRenderContext] {

  override def matches(actualContext: BlockRenderContext): Boolean = {
    lastStatContextMatches(actualContext)
  }

  private def lastStatContextMatches(actualContext: BlockRenderContext) = {
    new BlockStatRenderContextMockitoMatcher(expectedContext.lastStatContext).matches(actualContext.lastStatContext)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockRenderContextMockitoMatcher {
  def eqBlockRenderContext(expectedContext: BlockRenderContext): BlockRenderContext =
    argThat(new BlockRenderContextMockitoMatcher(expectedContext))
}

