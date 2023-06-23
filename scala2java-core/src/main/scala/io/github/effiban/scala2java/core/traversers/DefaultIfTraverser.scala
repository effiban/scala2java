package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, IfContext}
import io.github.effiban.scala2java.core.traversers.results.{BlockTraversalResult, IfTraversalResult}

import scala.meta.Term.If
import scala.meta.{Lit, Term}

trait DefaultIfTraverser {
  def traverse(`if`: If, context: IfContext = IfContext()): IfTraversalResult
}

private[traversers] class DefaultIfTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 blockWrappingTermTraverser: => BlockWrappingTermTraverser) extends DefaultIfTraverser {

  override def traverse(`if`: If, context: IfContext = IfContext()): IfTraversalResult = {
    val traversedCond = expressionTermTraverser.traverse(`if`.cond)
    val thenClauseResult = traverseClause(`if`.thenp, context)
    val maybeElseClauseResult = `if`.elsep match {
      case Lit.Unit() => None
      case elsep =>
        //TODO 1. If the 'then' clause returns a value, traverse the 'else' statement only (no 'else' word and no block)
        //TODO 2. If the 'else' clause is itself an 'if', don't wrap it in a block
        Some(traverseClause(elsep, context))
    }
    mergeResults(traversedCond, thenClauseResult, maybeElseClauseResult)
  }

  private def traverseClause(clause: Term, context: IfContext) = {
    blockWrappingTermTraverser.traverse(clause, BlockContext(shouldReturnValue = context.shouldReturnValue))
  }

  private def mergeResults(traversedCond: Term,
                           thenClauseResult: BlockTraversalResult,
                           maybeElseClauseResult: Option[BlockTraversalResult]) = {
    val traversedIf = If(
      cond = traversedCond,
      thenp = thenClauseResult.block,
      elsep = maybeElseClauseResult.map(_.block).getOrElse(Lit.Unit())
    )
    val uncertainReturn = thenClauseResult.uncertainReturn && maybeElseClauseResult.forall(_.uncertainReturn)
    IfTraversalResult(traversedIf, uncertainReturn = uncertainReturn)
  }
}
