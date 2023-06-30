package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term
import scala.meta.Term.{Block, TryWithHandler}

case class TryWithHandlerTraversalResult(exprResult: BlockTraversalResult,
                                         catchp: Term,
                                         maybeFinally: Option[Block] = None) extends BlockStatTraversalResult {
  override val stat: TryWithHandler = TryWithHandler(
    expr = exprResult.block,
    catchp = catchp,
    finallyp = maybeFinally
  )
}
