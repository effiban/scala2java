package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefTraverser extends ScalaTreeTraverser2[Term.Ref, Term]

private[traversers] class ExpressionTermRefTraverserImpl(expressionTermNameTraverser: => ExpressionTermNameTraverser,
                                                         expressionTermSelectTraverser: => ExpressionTermSelectTraverser,
                                                         applyUnaryTraverser: => ApplyUnaryTraverser,
                                                         defaultTermRefTraverser: => DefaultTermRefTraverser)
  extends ExpressionTermRefTraverser {

  override def traverse(termRef: Term.Ref): Term = termRef match {
    case termName: Term.Name => expressionTermNameTraverser.traverse(termName)
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case aTermRef => defaultTermRefTraverser.traverse(aTermRef)
  }
}
