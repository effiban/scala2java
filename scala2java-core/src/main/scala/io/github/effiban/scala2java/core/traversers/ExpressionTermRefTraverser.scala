package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

private[traversers] class ExpressionTermRefTraverser(termNameTraverser: => TermNameTraverser,
                                                     expressionTermSelectTraverser: => ExpressionTermSelectTraverser,
                                                     applyUnaryTraverser: => ApplyUnaryTraverser,
                                                     defaultTermRefTraverser: => TermRefTraverser) extends TermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case aTermRef => defaultTermRefTraverser.traverse(aTermRef)
  }
}
