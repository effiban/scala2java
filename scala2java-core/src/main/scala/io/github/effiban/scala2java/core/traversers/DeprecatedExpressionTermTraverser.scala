package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.{Block, If}
import scala.meta.{Term, XtensionQuasiquoteTerm}

/** Traverser for terms appearing in the context of an evaluated expression, such as:
 *   - RHS of an assigment
 *   - method argument
 *   - return value
 */

@deprecated
trait DeprecatedExpressionTermTraverser extends DeprecatedTermTraverser

@deprecated
private[traversers] class DeprecatedExpressionTermTraverserImpl(ifTraverser: => DeprecatedIfTraverser,
                                                                statTraverser: => StatTraverser,
                                                                termApplyTraverser: => DeprecatedTermApplyTraverser,
                                                                expressionTermRefTraverser: => DeprecatedExpressionTermRefTraverser,
                                                                defaultTermTraverser: => DeprecatedDefaultTermTraverser) extends DeprecatedExpressionTermTraverser {

  override def traverse(expression: Term): Unit = {
    expression match {
      case ref: Term.Ref => expressionTermRefTraverser.traverse(ref)
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
      case block: Block => termApplyTraverser.traverse(Term.Apply(Term.Select(Term.Function(Nil, block), q"apply"), Nil))
    }
  }
}