package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.{Block, If}

/** Traverser for terms appearing in the context of an evaluated expression, such as:
 *   - RHS of an assigment
 *   - method argument
 *   - return value
 */

trait ExpressionTermTraverser extends TermTraverser

private[traversers] class ExpressionTermTraverserImpl(expressionTermRefTraverser: => ExpressionTermRefTraverser,
                                                      expressionBlockTraverser: => ExpressionBlockTraverser,
                                                      expressionIfTraverser: => ExpressionIfTraverser,
                                                      defaultTermTraverser: => DefaultTermTraverser) extends ExpressionTermTraverser {

  override def traverse(expression: Term): Term = {
    expression match {
      case ref: Term.Ref => expressionTermRefTraverser.traverse(ref)
      case `if`: If => expressionIfTraverser.traverse(`if`)
      case block: Block => expressionBlockTraverser.traverse(block)
      case aTerm => defaultTermTraverser.traverse(aTerm)
    }
  }
}
