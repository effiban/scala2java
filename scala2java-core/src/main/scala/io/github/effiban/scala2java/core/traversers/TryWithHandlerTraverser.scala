package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, TryContext}
import io.github.effiban.scala2java.core.traversers.results.TryWithHandlerTraversalResult

import scala.meta.Term.TryWithHandler

trait TryWithHandlerTraverser {
  def traverse(`try`: TryWithHandler, context: TryContext = TryContext()): TryWithHandlerTraversalResult
}


private[traversers] class TryWithHandlerTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                                      finallyTraverser: => FinallyTraverser) extends TryWithHandlerTraverser {

  override def traverse(tryWithHandler: TryWithHandler, context: TryContext = TryContext()): TryWithHandlerTraversalResult = {
    import tryWithHandler._

    val exprResult = blockWrappingTermTraverser.traverse(expr, BlockContext(shouldReturnValue = context.shouldReturnValue))
    // TODO - The catch handler is some term which evaluates to a partial function, handle it once we have semantic information
    val maybeTraversedFinally = finallyp.map(finallyTraverser.traverse)

    TryWithHandlerTraversalResult(exprResult, catchp, maybeTraversedFinally)
  }
}
