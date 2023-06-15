package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Do

trait DoRenderer extends JavaTreeRenderer[Do]

private[renderers] class DoRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                        defaultTermRenderer: => DefaultTermRenderer)
                                       (implicit javaWriter: JavaWriter) extends DoRenderer {

  import javaWriter._

  override def render(`do`: Do): Unit = {
    write("do")
    defaultTermRenderer.render(`do`.body)
    write(" while (")
    expressionTermRenderer.render(`do`.expr)
    write(")")
  }
}
