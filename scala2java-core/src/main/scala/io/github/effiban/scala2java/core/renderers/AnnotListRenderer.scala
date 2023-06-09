package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Mod.Annot

trait AnnotListRenderer {
  def render(annotations: List[Annot], onSameLine: Boolean = false): Unit
}

private[renderers] class AnnotListRendererImpl(annotRenderer: => AnnotRenderer)
                                              (implicit javaWriter: JavaWriter) extends AnnotListRenderer {

  import javaWriter._

  override def render(annotations: List[Annot], onSameLine: Boolean = false): Unit = {
    annotations.foreach(annotation => {
      annotRenderer.render(annotation)
      if (onSameLine) {
        write(" ")
      } else {
        writeLine()
      }
    })
  }
}

