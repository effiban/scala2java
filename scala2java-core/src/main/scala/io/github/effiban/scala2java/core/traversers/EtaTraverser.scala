package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Eta

trait EtaTraverser extends ScalaTreeTraverser1[Eta]

private[traversers] class EtaTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends EtaTraverser {

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def traverse(eta: Eta): Eta = {
    // Java equivalent is a method reference, but it will only work if assigned to a typed variable on the LHS.
    // We also can't tell if it is an instance method or class method, so arbitrarily using `this`
    //TODO once semantic information is added, ensure correctness and support more complicated cases
    eta.copy(expr = expressionTermTraverser.traverse(eta.expr))
  }
}
