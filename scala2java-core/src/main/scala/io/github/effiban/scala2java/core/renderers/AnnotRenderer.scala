package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod.Annot

trait AnnotRenderer extends JavaTreeRenderer[Annot]

private[renderers] class AnnotRendererImpl(initRenderer: => InitRenderer)
                                          (implicit javaWriter: JavaWriter) extends AnnotRenderer {

  import javaWriter._

  override def render(annotation: Annot): Unit = {
    write("@")
    initRenderer.render(annotation.init)
  }
}
