package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermRepeatedTraverser extends ScalaTreeTraverser[Term.Repeated]

private[traversers] class TermRepeatedTraverserImpl(expressionTermTraverser: => TermTraverser) extends TermRepeatedTraverser {

  // Passing vararg param
  override def traverse(termRepeated: Term.Repeated): Unit = {
    //TODO may need to transform to array in Java
    expressionTermTraverser.traverse(termRepeated.expr)
  }
}
