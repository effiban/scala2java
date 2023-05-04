package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeWithRenderer extends JavaTreeRenderer[Type.With]

private[renderers] class TypeWithRendererImpl(typeRenderer: => TypeRenderer)
                                             (implicit javaWriter: JavaWriter) extends TypeWithRenderer {

  import javaWriter._

  // type with parent, e.g. 'A with B' in: type X = A with B
  // approximated by Java "extends" but might not compile
  override def render(typeWith: Type.With): Unit = {
    typeRenderer.render(typeWith.lhs)
    write(" extends ")
    typeRenderer.render(typeWith.rhs)
  }
}
