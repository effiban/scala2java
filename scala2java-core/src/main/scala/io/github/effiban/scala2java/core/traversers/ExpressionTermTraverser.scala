package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.{Block, If}

/** Traverser for terms appearing in the context of an evaluated expression, such as:
 *   - RHS of an assigment
 *   - method argument
 *   - return value
 */

trait ExpressionTermTraverser extends TermTraverser

private[traversers] class ExpressionTermTraverserImpl(ifTraverser: => IfTraverser,
                                                      statTraverser: => StatTraverser,
                                                      termApplyTraverser: => TermApplyTraverser,
                                                      expressionTermRefTraverser: => TermRefTraverser,
                                                      expressionMainApplyTypeTraverser: MainApplyTypeTraverser,
                                                      defaultTermTraverser: => DefaultTermTraverser) extends ExpressionTermTraverser {

  override def traverse(expression: Term): Unit = {
    expression match {
      case ref: Term.Ref => expressionTermRefTraverser.traverse(ref)
      case applyType: Term.ApplyType => expressionMainApplyTypeTraverser.traverse(applyType)
      case `if`: If => ifTraverser.traverseAsTertiaryOp(`if`)
      case block: Block => traverseBlock(block)
      case aTerm => defaultTermTraverser.traverse(aTerm)
    }
  }

  private def traverseBlock(block: Block): Unit = {
    // A Block is not a valid expression in Java, so we need to transform it before traversal
    block match {
      // When it has one statement - unwrapping it to the statement
      case Block(List(stat)) => statTraverser.traverse(stat)
      // When it has more than one statement - wrapping it with a zero-arg lambda which is immediately invoked
      case block: Block => termApplyTraverser.traverse(Term.Apply(Term.Function(Nil, block), Nil))
    }
  }
}
