package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.If

trait RhsTermTraverser extends ScalaTreeTraverser[Term]

private[traversers] class RhsTermTraverserImpl(ifTraverser: => IfTraverser,
                                               termTraverser: => TermTraverser) extends RhsTermTraverser {

  override def traverse(term: Term): Unit = {
    term match {
      case `if`: If => ifTraverser.traverseAsTertiaryOp(`if`)
      case aTerm => termTraverser.traverse(aTerm)
    }
  }
}
