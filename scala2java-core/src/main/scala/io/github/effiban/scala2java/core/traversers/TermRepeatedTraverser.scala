package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermRepeatedTraverser extends ScalaTreeTraverser1[Term.Repeated]

private[traversers] class TermRepeatedTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends TermRepeatedTraverser {

  // Passing vararg param
  override def traverse(termRepeated: Term.Repeated): Term.Repeated = {
    //TODO may need to transform to array in Java
    termRepeated.copy(expr = expressionTermTraverser.traverse(termRepeated.expr))
  }
}
