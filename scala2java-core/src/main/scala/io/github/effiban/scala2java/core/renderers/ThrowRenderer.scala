package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Throw

trait ThrowRenderer extends JavaTreeRenderer[Throw]

private[renderers] class ThrowRendererImpl(expressionTermRenderer: => ExpressionTermRenderer)
                                          (implicit javaWriter: JavaWriter) extends ThrowRenderer {

  import javaWriter._

  override def render(`throw`: Throw): Unit = {
    write("throw ")
    expressionTermRenderer.render(`throw`.expr)
  }
}