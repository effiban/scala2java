package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Init

class BlockRenderContextMockitoMatcher(expectedContext: BlockRenderContext) extends ArgumentMatcher[BlockRenderContext] {

  override def matches(actualContext: BlockRenderContext): Boolean = {
    lastStatContextMatches(actualContext) && maybeInitMatches(actualContext)
  }

  private def lastStatContextMatches(actualContext: BlockRenderContext) = {
    new BlockStatRenderContextMockitoMatcher(expectedContext.lastStatContext).matches(actualContext.lastStatContext)
  }

  private def maybeInitMatches(actualContext: BlockRenderContext) = {
    new OptionMatcher[Init](expectedContext.maybeInit, new TreeMatcher[Init](_)).matches(actualContext.maybeInit)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockRenderContextMockitoMatcher {
  def eqBlockRenderContext(expectedContext: BlockRenderContext): BlockRenderContext =
    argThat(new BlockRenderContextMockitoMatcher(expectedContext))
}

