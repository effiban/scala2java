package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.TryRenderContext
import io.github.effiban.scala2java.core.traversers.results.{TryTraversalResult, TryWithHandlerTraversalResult}

trait TryRenderContextFactory extends Function[TryTraversalResult, TryRenderContext] {
  def apply(tryWithHandlerTraversalResult: TryWithHandlerTraversalResult): TryRenderContext
}


private[contextfactories] class TryRenderContextFactoryImpl(blockRenderContextFactory: => BlockRenderContextFactory)
  extends TryRenderContextFactory {

  def apply(traversalResult: TryTraversalResult): TryRenderContext = {
    val exprContext = blockRenderContextFactory(traversalResult.exprResult)
    val catchContexts = traversalResult.catchResults.map(_.bodyResult).map(blockRenderContextFactory)
    TryRenderContext(exprContext = exprContext, catchContexts = catchContexts)
  }

  def apply(traversalResult: TryWithHandlerTraversalResult): TryRenderContext = {
    val exprContext = blockRenderContextFactory(traversalResult.exprResult)
    TryRenderContext(exprContext = exprContext)
  }
}
