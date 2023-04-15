package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeNameRenderer extends TreeRenderer[Type.Name]

private[renderers] class TypeNameRendererImpl(implicit javaWriter: JavaWriter) extends TypeNameRenderer {

  import javaWriter._

  override def render(typeName: Type.Name): Unit = write(typeName.value)
}
