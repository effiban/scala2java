package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, IfContext}
import io.github.effiban.scala2java.core.traversers.results.IfTraversalResult

import scala.meta.Term.If
import scala.meta.{Lit, Term}

trait DefaultIfTraverser {
  def traverse(`if`: If, context: IfContext = IfContext()): IfTraversalResult
}

private[traversers] class DefaultIfTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                                 blockWrappingTermTraverser: => BlockWrappingTermTraverser) extends DefaultIfTraverser {

  override def traverse(`if`: If, context: IfContext = IfContext()): IfTraversalResult = {
    val traversedCond = expressionTermTraverser.traverse(`if`.cond)
    val thenpResult = traverseClause(`if`.thenp, context)
    val maybeElsepResult = `if`.elsep match {
      case Lit.Unit() => None
      case elsep =>
        //TODO 1. If the 'then' clause returns a value, traverse the 'else' statement only (no 'else' word and no block)
        //TODO 2. If the 'else' clause is itself an 'if', don't wrap it in a block
        Some(traverseClause(elsep, context))
    }
    IfTraversalResult(
      If(
        cond = traversedCond,
        thenp = thenpResult.block,
        elsep = maybeElsepResult.map(_.block).getOrElse(Lit.Unit())
      )
    )
  }

  private def traverseClause(clause: Term, context: IfContext) = {
    blockWrappingTermTraverser.traverse(clause, BlockContext(shouldReturnValue = context.shouldReturnValue))
  }
}
