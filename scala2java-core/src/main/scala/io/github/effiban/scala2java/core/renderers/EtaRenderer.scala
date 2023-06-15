package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Eta

trait EtaRenderer extends JavaTreeRenderer[Eta]

private[renderers] class EtaRendererImpl(expressionTermRenderer: => ExpressionTermRenderer)
                                        (implicit javaWriter: JavaWriter) extends EtaRenderer {

  import javaWriter._

  // Eta expansion: Method translated into function, e.g.:  myMethod _
  def render(eta: Term.Eta): Unit = {
    // Java equivalent is a method reference, but it will only work if assigned to a typed variable on the LHS.
    // We also can't tell if it is an instance method or class method, so arbitrarily using `this`
    //TODO if type inference is added later on, use that to ensure correctness
    write("this::")
    expressionTermRenderer.render(eta.expr)
  }
}
