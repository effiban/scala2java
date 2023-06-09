package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Return

trait ReturnRenderer extends JavaTreeRenderer[Return]

private[renderers] class ReturnRendererImpl(expressionTermRenderer: => ExpressionTermRenderer)
                                           (implicit javaWriter: JavaWriter) extends ReturnRenderer {

  import javaWriter._

  override def render(`return`: Return): Unit = {
    write("return ")
    expressionTermRenderer.render(`return`.expr)
  }
}
