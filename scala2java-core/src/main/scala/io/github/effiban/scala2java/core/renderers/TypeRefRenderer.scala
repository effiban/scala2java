package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRefRenderer extends JavaTreeRenderer[Type.Ref]

private[renderers] class TypeRefRendererImpl(typeNameRenderer: TypeNameRenderer,
                                             typeSelectRenderer: => TypeSelectRenderer,
                                             typeProjectRenderer: => TypeProjectRenderer,
                                             typeSingletonRenderer: TypeSingletonRenderer)
                                            (implicit javaWriter: JavaWriter) extends TypeRefRenderer {

  import javaWriter._

  override def render(typeRef: Type.Ref): Unit = typeRef match {
    case typeName: Type.Name => typeNameRenderer.render(typeName)
    case typeSelect: Type.Select => typeSelectRenderer.render(typeSelect)
    case typeProject: Type.Project => typeProjectRenderer.render(typeProject)
    case typeSingleton: Type.Singleton => typeSingletonRenderer.render(typeSingleton)
    case _ => writeComment(s"UNSUPPORTED: $typeRef")
  }
}
