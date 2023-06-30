package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.TryWithHandlerTraversalResult

import scala.meta.Term.{Block, TryWithHandler}

object TestableTryWithHandlerTraversalResult {

  def apply(tryWithHandler: TryWithHandler, exprUncertainReturn: Boolean = false): TryWithHandlerTraversalResult = {
    val exprResult = tryWithHandler.expr match {
      case blockExpr: Block => TestableBlockTraversalResult(blockExpr, exprUncertainReturn)
      case nonBlockExpr => throw new IllegalArgumentException(
        s"A TryTraversalResult must contain an 'expr' clause which is a Block, but the given 'expr' clause is: $nonBlockExpr"
      )
    }

    val maybeFinally = tryWithHandler.finallyp.map {
      case finallyBlock: Block => finallyBlock
      case nonBlockFinally => throw new IllegalArgumentException(
        s"A TryTraversalResult must cointain a finally block, but the given finally clause is: $nonBlockFinally"
      )
    }
    TryWithHandlerTraversalResult(exprResult, tryWithHandler.catchp, maybeFinally)
  }
}
