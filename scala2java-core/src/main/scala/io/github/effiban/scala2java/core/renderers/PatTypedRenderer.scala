package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Pat

trait PatTypedRenderer extends JavaTreeRenderer[Pat.Typed]

private[renderers] class PatTypedRendererImpl(typeRenderer: => TypeRenderer,
                                              patRenderer: => PatRenderer)
                                             (implicit javaWriter: JavaWriter) extends PatTypedRenderer {

  import javaWriter._

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def render(typedPattern: Pat.Typed): Unit = {
    typeRenderer.render(typedPattern.rhs)
    write(" ")
    patRenderer.render(typedPattern.lhs)
  }
}
