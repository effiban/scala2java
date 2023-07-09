package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}

import scala.meta.Case

trait CatchHandlerTraverser {
  def traverse(catchCase: Case,
               context: CatchHandlerContext = CatchHandlerContext()): Case
}

private[traversers] class CatchHandlerTraverserImpl(catchArgumentTraverser: => CatchArgumentTraverser,
                                                    blockWrappingTermTraverser: BlockWrappingTermTraverser) extends CatchHandlerTraverser {

  override def traverse(catchCase: Case,
                        context: CatchHandlerContext = CatchHandlerContext()): Case = {
    val traversedPat = catchArgumentTraverser.traverse(catchCase.pat)
    // TODO - handle cond by moving into body
    val bodyTraversalResult = blockWrappingTermTraverser.traverse(catchCase.body,
      context = BlockContext(shouldReturnValue = context.shouldReturnValue))
    Case(pat = traversedPat, cond = None, body = bodyTraversalResult)
  }
}
