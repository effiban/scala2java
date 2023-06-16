package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

@deprecated
trait DeprecatedTermRepeatedTraverser extends ScalaTreeTraverser[Term.Repeated]

@deprecated
private[traversers] class DeprecatedTermRepeatedTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser) extends DeprecatedTermRepeatedTraverser {

  // Passing vararg param
  override def traverse(termRepeated: Term.Repeated): Unit = {
    //TODO may need to transform to array in Java
    expressionTermTraverser.traverse(termRepeated.expr)
  }
}
