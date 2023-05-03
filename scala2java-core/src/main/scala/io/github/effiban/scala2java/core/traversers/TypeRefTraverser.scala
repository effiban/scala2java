package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeRefRenderer

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser[Type.Ref]

private[traversers] class TypeRefTraverserImpl(typeNameTraverser: TypeNameTraverser,
                                               typeSelectTraverser: TypeSelectTraverser,
                                               typeProjectTraverser: => TypeProjectTraverser,
                                               typeSingletonTraverser: TypeSingletonTraverser,
                                               typeRefRenderer: => TypeRefRenderer) extends TypeRefTraverser {

  override def traverse(typeRef: Type.Ref): Unit = {
    val traversedTypeRef = typeRef match {
      case typeName: Type.Name => typeNameTraverser.traverse(typeName)
      case typeSelect: Type.Select => typeSelectTraverser.traverse(typeSelect)
      case typeSingleton: Type.Singleton => typeSingletonTraverser.traverse(typeSingleton)
      case aTypeRef => aTypeRef
    }

    traversedTypeRef match {
      case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
      case aTypeRef => typeRefRenderer.render(aTypeRef)
    }
  }
}
