package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeSelectRenderer extends JavaTreeRenderer[Type.Select]

private[renderers] class TypeSelectRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                                typeNameRenderer: TypeNameRenderer)
                                               (implicit javaWriter: JavaWriter) extends TypeSelectRenderer {

  import javaWriter._

  // A scala type selecting a type from a term, e.g.: a.B where 'a' is of some class 'A' that has a 'B' type inside.
  // I think it's supported in Java, but will produce a warning that a 'static member is being accessed from a non-static context'.
  override def render(typeSelect: Type.Select): Unit = {
    defaultTermRefRenderer.render(typeSelect.qual)
    writeQualifierSeparator()
    typeNameRenderer.render(typeSelect.name)
  }
}
