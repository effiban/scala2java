package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext}
import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult

trait IfRenderContextFactory extends Function[IfTraversalResult, IfRenderContext]

private[contextfactories] class IfRenderContextFactoryImpl(blockRenderContextFactory: => BlockRenderContextFactory)
  extends IfRenderContextFactory {

  def apply(traversalResult: IfTraversalResult): IfRenderContext = {
    val thenContext = blockRenderContextFactory(traversalResult.thenpResult)
    val maybeElseContext = traversalResult.maybeElsepResult.map(blockRenderContextFactory)
    IfRenderContext(thenContext = thenContext, elseContext = maybeElseContext.getOrElse(BlockRenderContext()))
  }

}
