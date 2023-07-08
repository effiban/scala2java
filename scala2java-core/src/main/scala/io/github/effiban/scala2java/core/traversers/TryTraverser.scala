package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}
import io.github.effiban.scala2java.core.traversers.results.TryTraversalResult

import scala.meta.{Case, Term}

trait TryTraverser {
  def traverse(`try`: Term.Try, context: TryContext = TryContext()): TryTraversalResult
}


private[traversers] class TryTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser) extends TryTraverser {

  override def traverse(`try`: Term.Try, context: TryContext = TryContext()): TryTraversalResult = {
    import `try`._

    val exprResult = blockWrappingTermTraverser.traverse(expr, BlockContext(shouldReturnValue = context.shouldReturnValue))

    val catchHandlerResults = catchp.map(`case` =>
      catchHandlerTraverser.traverse(
        catchCase = `case`,
        context = CatchHandlerContext(shouldReturnValue = context.shouldReturnValue)
      )
    )

    val maybeTraversedFinally = finallyp.map(finallyTraverser.traverse)

    val traversedTry = Term.Try(
      expr = exprResult.block,
      catchp = catchHandlerResults.map(res =>
        Case(pat = res.pat, cond = None, body = res.bodyResult.block)
      ),
      finallyp = maybeTraversedFinally
    )

    TryTraversalResult(traversedTry)
  }
}
