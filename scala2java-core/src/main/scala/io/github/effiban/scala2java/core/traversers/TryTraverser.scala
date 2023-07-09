package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}

import scala.meta.Term

trait TryTraverser {
  def traverse(`try`: Term.Try, context: TryContext = TryContext()): Term.Try
}


private[traversers] class TryTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser) extends TryTraverser {

  override def traverse(`try`: Term.Try, context: TryContext = TryContext()): Term.Try = {
    import `try`._

    val exprResult = blockWrappingTermTraverser.traverse(expr, BlockContext(shouldReturnValue = context.shouldReturnValue))

    val traversedCatchCases = catchp.map(`case` =>
      catchHandlerTraverser.traverse(
        catchCase = `case`,
        context = CatchHandlerContext(shouldReturnValue = context.shouldReturnValue)
      )
    )

    val maybeTraversedFinally = finallyp.map(finallyTraverser.traverse)

    Term.Try(
      expr = exprResult.block,
      catchp = traversedCatchCases,
      finallyp = maybeTraversedFinally
    )
  }
}
