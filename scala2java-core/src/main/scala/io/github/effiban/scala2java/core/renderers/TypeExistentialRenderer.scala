package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeExistentialRenderer extends JavaTreeRenderer[Type.Existential]

private[renderers] class TypeExistentialRendererImpl(typeRenderer: => TypeRenderer)
                                                    (implicit javaWriter: JavaWriter) extends TypeExistentialRenderer {

  import javaWriter._

  // type with existential constraint e.g.:  A[B] forSome {B <: Number with Serializable}
  override def render(existentialType: Type.Existential): Unit = {
    typeRenderer.render(existentialType.tpe)
    //TODO - convert to Java for simple cases
    writeComment(s"forSome ${existentialType.stats.toString()}")
  }
}
