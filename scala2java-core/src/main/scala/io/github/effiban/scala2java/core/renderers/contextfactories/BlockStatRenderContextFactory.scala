package io.github.effiban.scala2java.core.renderers.contextfactories

import io.github.effiban.scala2java.core.contexts.{BlockStatRenderContext, SimpleBlockStatRenderContext}
import io.github.effiban.scala2java.core.traversers.results._

trait BlockStatRenderContextFactory extends Function[BlockStatTraversalResult, BlockStatRenderContext]

private[contextfactories] class BlockStatRenderContextFactoryImpl(ifRenderContextFactory: => IfRenderContextFactory,
                                                                  tryRenderContextFactory: => TryRenderContextFactory)
  extends BlockStatRenderContextFactory {

  def apply(traversalResult: BlockStatTraversalResult): BlockStatRenderContext = traversalResult match {
    case simpleResult: SimpleBlockStatTraversalResult => SimpleBlockStatRenderContext(simpleResult.uncertainReturn)
    case ifResult: IfTraversalResult => ifRenderContextFactory(ifResult)
    case tryResult: TryTraversalResult => tryRenderContextFactory(tryResult)
    case tryWithHandlerResult: TryWithHandlerTraversalResult => tryRenderContextFactory(tryWithHandlerResult)
  }
}
