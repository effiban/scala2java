package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRepeatedRenderer extends JavaTreeRenderer[Type.Repeated]

private[renderers] class TypeRepeatedRendererImpl(typeRenderer: => TypeRenderer)
                                                 (implicit javaWriter: JavaWriter) extends TypeRepeatedRenderer {

  import javaWriter._

  // Vararg type,e.g.: T*
  override def render(repeatedType: Type.Repeated): Unit = {
    typeRenderer.render(repeatedType.tpe)
    writeEllipsis()
  }
}
