package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefTraverser extends TermRefTraverser

private[traversers] class ExpressionTermRefTraverserImpl(defaultTermRefTraverser: => DefaultTermRefTraverser)
  extends ExpressionTermRefTraverser {

  override def traverse(termRef: Term.Ref): Term.Ref = termRef match {
    case termName: Term.Name => termName // TODO
    case termSelect: Term.Select => termSelect // TODO
    case applyUnary: ApplyUnary => applyUnary // TODO
    case aTermRef => defaultTermRefTraverser.traverse(aTermRef)
  }
}
