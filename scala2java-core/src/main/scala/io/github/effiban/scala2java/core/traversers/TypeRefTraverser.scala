package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser1[Type.Ref]

private[traversers] class TypeRefTraverserImpl(typeNameTraverser: TypeNameTraverser,
                                               typeSelectTraverser: TypeSelectTraverser,
                                               typeSingletonTraverser: TypeSingletonTraverser,
                                               typeProjectTraverser: => TypeProjectTraverser) extends TypeRefTraverser {

  override def traverse(typeRef: Type.Ref): Type.Ref = typeRef match {
      case typeName: Type.Name => typeNameTraverser.traverse(typeName)
      case typeSelect: Type.Select => typeSelectTraverser.traverse(typeSelect)
      case typeSingleton: Type.Singleton => typeSingletonTraverser.traverse(typeSingleton)
      case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
      case aTypeRef => aTypeRef
    }
}
