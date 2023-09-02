package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser2[Type.Ref, Type]

private[traversers] class TypeRefTraverserImpl(typeSelectTraverser: TypeSelectTraverser,
                                               typeProjectTraverser: => TypeProjectTraverser) extends TypeRefTraverser {

  override def traverse(typeRef: Type.Ref): Type = typeRef match {
      case typeName: Type.Name => typeName
      case typeSelect: Type.Select => typeSelectTraverser.traverse(typeSelect)
      case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
      case aTypeRef => aTypeRef
    }
}
