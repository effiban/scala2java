package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.traversers.results.TryTraversalResult

import scala.meta.Term
import scala.meta.Term.Block

object TestableTryTraversalResult {

  def apply(termTry: Term.Try,
            exprUncertainReturn: Boolean = false,
            catchUncertainReturns: List[Boolean] = Nil): TryTraversalResult = {
    val exprResult = termTry.expr match {
      case blockExpr: Block => TestableBlockTraversalResult(blockExpr, exprUncertainReturn)
      case nonBlockExpr => throw new IllegalArgumentException(
        s"A TryTraversalResult must contain an 'expr' clause which is a Block, but the given 'expr' clause is: $nonBlockExpr"
      )
    }

    if (termTry.catchp.length != catchUncertainReturns.length) {
      throw new IllegalArgumentException(
        s"The input 'Try' has ${termTry.catchp.length} catch clauses, " +
          s"while the input 'catchUncertainReturns' list has ${catchUncertainReturns.length} elems"
      )
    }

    val catchResults = termTry.catchp.zipWithIndex.map { case (catchCase, idx) =>
      TestableCatchHandlerTraversalResult(catchCase, catchUncertainReturns(idx))
    }

    val maybeFinally = termTry.finallyp.map {
      case finallyBlock: Block => finallyBlock
      case nonBlockFinally => throw new IllegalArgumentException(
        s"A TryTraversalResult must cointain a finally block, but the given finally clause is: $nonBlockFinally"
      )
    }
    TryTraversalResult(exprResult, catchResults, maybeFinally)
  }
}
