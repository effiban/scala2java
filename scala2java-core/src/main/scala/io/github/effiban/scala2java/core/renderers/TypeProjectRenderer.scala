package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeProjectRenderer extends JavaTreeRenderer[Type.Project]

private[renderers] class TypeProjectRendererImpl(typeRenderer: => TypeRenderer,
                                                 typeNameRenderer: TypeNameRenderer)
                                                (implicit javaWriter: JavaWriter) extends TypeProjectRenderer {

  import javaWriter._

  override def render(typeProject: Type.Project): Unit = {
    typeRenderer.render(typeProject.qual)
    writeQualifierSeparator()
    typeNameRenderer.render(typeProject.name)
  }
}
