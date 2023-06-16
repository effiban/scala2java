package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

@deprecated
trait DeprecatedStatTermTraverser extends DeprecatedTermTraverser

@deprecated
private[traversers] class DeprecatedStatTermTraverserImpl(expressionTermRefTraverser: => DeprecatedExpressionTermRefTraverser,
                                                          defaultTermTraverser: => DeprecatedDefaultTermTraverser) extends DeprecatedStatTermTraverser {

  override def traverse(fun: Term): Unit = fun match {
    case ref: Term.Ref => expressionTermRefTraverser.traverse(ref)
    case term => defaultTermTraverser.traverse(term)
  }
}
