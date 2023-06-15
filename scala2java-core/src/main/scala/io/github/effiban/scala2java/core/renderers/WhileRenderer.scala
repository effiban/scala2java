package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.While

trait WhileRenderer extends JavaTreeRenderer[While]

private[renderers] class WhileRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                           defaultTermRenderer: => DefaultTermRenderer)
                                          (implicit javaWriter: JavaWriter) extends WhileRenderer {

  import javaWriter._

  override def render(`while`: While): Unit = {
    write("while (")
    expressionTermRenderer.render(`while`.expr)
    write(")")
    defaultTermRenderer.render(`while`.body)
  }
}
