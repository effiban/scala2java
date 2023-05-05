package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeWildcardRenderer extends JavaTreeRenderer[Type.Wildcard]

private[renderers] class TypeWildcardRendererImpl(typeBoundsRenderer: => TypeBoundsRenderer)
                                                 (implicit javaWriter: JavaWriter) extends TypeWildcardRenderer {

  import javaWriter._

  // Underscore in type param, e.g. T[_] with possible bounds e.g. T[_ <: A]
  override def render(wildcardType: Type.Wildcard): Unit = {
    write("?")
    typeBoundsRenderer.render(wildcardType.bounds)
  }
}
