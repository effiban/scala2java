package io.github.effiban.scala2java.core.matchers

import io.github.effiban.scala2java.core.contexts.BlockRenderContext
import io.github.effiban.scala2java.test.utils.matchers.{OptionMatcher, TreeMatcher}
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.argThat

import scala.meta.Init

class BlockRenderContextMatcher(expectedContext: BlockRenderContext) extends ArgumentMatcher[BlockRenderContext] {

  override def matches(actualContext: BlockRenderContext): Boolean = {
    actualContext.uncertainReturn == expectedContext.uncertainReturn && maybeInitMatches(actualContext)
  }

  private def maybeInitMatches(actualContext: BlockRenderContext) = {
    new OptionMatcher[Init](expectedContext.maybeInit, new TreeMatcher[Init](_)).matches(actualContext.maybeInit)
  }

  override def toString: String = s"Matcher for: $expectedContext"
}

object BlockRenderContextMatcher {
  def eqBlockRenderContext(expectedContext: BlockRenderContext): BlockRenderContext = argThat(new BlockRenderContextMatcher(expectedContext))
}

