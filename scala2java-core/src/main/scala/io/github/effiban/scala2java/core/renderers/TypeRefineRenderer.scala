package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRefineRenderer extends JavaTreeRenderer[Type.Refine]

private[renderers] class TypeRefineRendererImpl(typeRenderer: => TypeRenderer)
                                               (implicit javaWriter: JavaWriter) extends TypeRefineRenderer {

  import javaWriter._

  // Scala feature which allows to extend the definition of a type, e.g. the block in the RHS below:
  // type B = A {def f: Int}
  override def render(refinedType: Type.Refine): Unit = {
    refinedType.tpe.foreach(typeRenderer.render)
    //TODO maybe convert to Java type with inheritance
    writeComment(s"${refinedType.stats.toString()}")
  }
}
