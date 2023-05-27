package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait StatTermTraverser extends TermTraverser

private[traversers] class StatTermTraverserImpl(expressionTermRefTraverser: => ExpressionTermRefTraverser,
                                                defaultTermTraverser: => DefaultTermTraverser) extends StatTermTraverser {

  override def traverse(fun: Term): Unit = fun match {
    case ref: Term.Ref => expressionTermRefTraverser.traverse(ref)
    case term => defaultTermTraverser.traverse(term)
  }
}
