package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.{Type, XtensionQuasiquoteType}

trait TypeSelectRenderer extends JavaTreeRenderer[Type.Select]

private[renderers] class TypeSelectRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer,
                                                typeNameRenderer: TypeNameRenderer,
                                                arrayTypeRenderer: => ArrayTypeRenderer)
                                               (implicit javaWriter: JavaWriter) extends TypeSelectRenderer {

  import javaWriter._

  // A scala type selecting a type from a term, e.g.: a.B where 'a' is of some class 'A' that has a 'B' type inside.
  override def render(typeSelect: Type.Select): Unit = typeSelect match {
    // TODO - remove this, a Scala Array must always have a type
    case t"scala.Array" => arrayTypeRenderer.render(t"Object")
    case aTypeSelect =>
      defaultTermRefRenderer.render(aTypeSelect.qual)
      writeQualifierSeparator()
      typeNameRenderer.render(aTypeSelect.name)
  }
}
