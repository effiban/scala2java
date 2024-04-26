package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefTraverser extends ScalaTreeTraverser2[Term.Ref, Term]

private[traversers] class ExpressionTermRefTraverserImpl(expressionTermSelectTraverser: => ExpressionTermSelectTraverser,
                                                         applyUnaryTraverser: => ApplyUnaryTraverser,
                                                         defaultTermRefTraverser: => DefaultTermRefTraverser)
  extends ExpressionTermRefTraverser {

  override def traverse(termRef: Term.Ref): Term = termRef match {
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case aTermRef => defaultTermRefTraverser.traverse(aTermRef)
  }
}
