package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Eta

@deprecated
trait DeprecatedEtaTraverser extends ScalaTreeTraverser[Eta]

@deprecated
private[traversers] class DeprecatedEtaTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser)
                                                    (implicit javaWriter: JavaWriter) extends DeprecatedEtaTraverser {

  import javaWriter._

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def traverse(eta: Term.Eta): Unit = {
    // Java equivalent is a method reference, but it will only work if assigned to a typed variable on the LHS.
    // We also can't tell if it is an instance method or class method, so arbitrarily using `this`
    //TODO if type inference is added later on, use that to ensure correctness
    write("this::")
    expressionTermTraverser.traverse(eta.expr)
  }
}
