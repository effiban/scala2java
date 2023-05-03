package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.{TypeNameRenderer, TypeSelectRenderer, TypeSingletonRenderer}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser[Type.Ref]

private[traversers] class TypeRefTraverserImpl(typeNameTraverser: TypeNameTraverser,
                                               typeNameRenderer: TypeNameRenderer,
                                               typeSelectTraverser: TypeSelectTraverser,
                                               typeSelectRenderer: TypeSelectRenderer,
                                               typeProjectTraverser: => TypeProjectTraverser,
                                               typeSingletonTraverser: TypeSingletonTraverser,
                                               typeSingletonRenderer: TypeSingletonRenderer)
                                              (implicit javaWriter: JavaWriter) extends TypeRefTraverser {

  import javaWriter._

  override def traverse(typeRef: Type.Ref): Unit = typeRef match {
    case typeName: Type.Name =>
      val traversedTypeName = typeNameTraverser.traverse(typeName)
      typeNameRenderer.render(traversedTypeName)
    case typeSelect: Type.Select =>
      val traversedTypeSelect = typeSelectTraverser.traverse(typeSelect)
      typeSelectRenderer.render(traversedTypeSelect)
    case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
    case typeSingleton: Type.Singleton =>
      val traversedTypeSingleton = typeSingletonTraverser.traverse(typeSingleton)
      typeSingletonRenderer.render(traversedTypeSingleton)
    case _ => writeComment(s"UNSUPPORTED: $typeRef")
  }
}
