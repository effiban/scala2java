package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult

trait BlockRenderContextFactory extends Function[BlockTraversalResult, BlockRenderContext]

private[contextfactories] class BlockRenderContextFactoryImpl(blockStatRenderContextFactory: => BlockStatRenderContextFactory)
  extends BlockRenderContextFactory {

  def apply(traversalResult: BlockTraversalResult): BlockRenderContext = {
    val lastStatContext = traversalResult.maybeLastStatResult
      .map(blockStatRenderContextFactory)
      .getOrElse(SimpleBlockStatRenderContext())
    val maybeInit = traversalResult.maybeInit
    BlockRenderContext(lastStatContext = lastStatContext, maybeInit = maybeInit)
  }
}
