package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}
import io.github.effiban.scala2java.core.traversers.results.CatchHandlerTraversalResult

import scala.meta.Case

trait CatchHandlerTraverser {
  def traverse(catchCase: Case,
               context: CatchHandlerContext = CatchHandlerContext()): CatchHandlerTraversalResult
}

private[traversers] class CatchHandlerTraverserImpl(catchArgumentTraverser: => CatchArgumentTraverser,
                                                    blockWrappingTermTraverser: BlockWrappingTermTraverser) extends CatchHandlerTraverser {

  override def traverse(catchCase: Case,
                        context: CatchHandlerContext = CatchHandlerContext()): CatchHandlerTraversalResult = {
    val traversedPat = catchArgumentTraverser.traverse(catchCase.pat)
    // TODO - handle cond by moving into body
    val bodyTraversalResult = blockWrappingTermTraverser.traverse(catchCase.body,
      context = BlockContext(shouldReturnValue = context.shouldReturnValue))
    CatchHandlerTraversalResult(pat = traversedPat, bodyTraversalResult)
  }
}
