package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeBoundsRenderer extends JavaTreeRenderer[Type.Bounds]

private[renderers] class TypeBoundsRendererImpl(typeRenderer: => TypeRenderer)
                                               (implicit javaWriter: JavaWriter) extends TypeBoundsRenderer {

  import javaWriter._

  // Scala type bounds e.g. X <: Y
  override def render(typeBounds: Type.Bounds): Unit = {
    //Only upper or lower bounds allowed in Java, not both
    //TODO - call the renderer with an argument indicating that Java primitives should be boxed
    (typeBounds.lo, typeBounds.hi) match {
      case (Some(lo), None) =>
        write(" super ")
        typeRenderer.render(lo)
      case (None, Some(hi)) =>
        write(" extends ")
        typeRenderer.render(hi)
      case (None, None) =>
      case _ =>
        // Both bounds provided - we can only write a comment
        writeComment(typeBounds.toString)
    }
  }
}
