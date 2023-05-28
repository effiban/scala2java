package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.DefaultTermRefRenderer

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefTraverser extends TermRefTraverser

private[traversers] class ExpressionTermRefTraverserImpl(termNameTraverser: => TermNameTraverser,
                                                         expressionTermSelectTraverser: => ExpressionTermSelectTraverser,
                                                         applyUnaryTraverser: => ApplyUnaryTraverser,
                                                         defaultTermRefTraverser: => DefaultTermRefTraverser,
                                                         defaultTermRefRenderer: => DefaultTermRefRenderer)
  extends ExpressionTermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => expressionTermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case aTermRef =>
      val traversedTermRef = defaultTermRefTraverser.traverse(aTermRef)
      defaultTermRefRenderer.render(traversedTermRef)
  }
}
