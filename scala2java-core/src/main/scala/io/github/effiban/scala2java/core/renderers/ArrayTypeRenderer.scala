package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.SquareBracket
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait ArrayTypeRenderer extends JavaTreeRenderer[Type]

private[renderers] class ArrayTypeRendererImpl(typeRenderer: => TypeRenderer)
                                              (implicit javaWriter: JavaWriter) extends ArrayTypeRenderer {

  import javaWriter._

  override def render(tpe: Type): Unit = {
    typeRenderer.render(tpe)
    writeStartDelimiter(SquareBracket)
    writeEndDelimiter(SquareBracket)
  }
}
