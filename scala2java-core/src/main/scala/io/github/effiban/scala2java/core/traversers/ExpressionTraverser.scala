package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.If

trait ExpressionTraverser extends ScalaTreeTraverser[Term]

private[traversers] class ExpressionTraverserImpl(ifTraverser: => IfTraverser,
                                                  termTraverser: => TermTraverser) extends ExpressionTraverser {

  override def traverse(expression: Term): Unit = {
    expression match {
      case `if`: If => ifTraverser.traverseAsTertiaryOp(`if`)
      case aTerm => termTraverser.traverse(aTerm)
    }
  }
}
