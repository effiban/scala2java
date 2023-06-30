package io.github.effiban.scala2java.core.traversers.results

import scala.meta.Term.Block
import scala.meta.{Case, Term}

case class TryTraversalResult(exprResult: BlockTraversalResult,
                              catchResults: List[CatchHandlerTraversalResult] = Nil,
                              maybeFinally: Option[Block] = None) extends BlockStatTraversalResult {
  override val stat: Term.Try = Term.Try(
    expr = exprResult.block,
    catchp = catchResults.map(result => Case(pat = result.pat, cond = None, body = result.bodyResult.block)),
    finallyp = maybeFinally
  )
}
