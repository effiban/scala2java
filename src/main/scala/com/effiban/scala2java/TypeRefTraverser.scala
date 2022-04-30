package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser[Type.Ref]

private[scala2java] class TypeRefTraverserImpl(typeNameTraverser: => TypeNameTraverser,
                                               typeSelectTraverser: => TypeSelectTraverser,
                                               typeProjectTraverser: => TypeProjectTraverser,
                                               typeSingletonTraverser: => TypeSingletonTraverser) extends TypeRefTraverser {

  override def traverse(typeRef: Type.Ref): Unit = typeRef match {
    case typeName: Type.Name => typeNameTraverser.traverse(typeName)
    case typeSelect: Type.Select => typeSelectTraverser.traverse(typeSelect)
    case typeProject: Type.Project => typeProjectTraverser.traverse(typeProject)
    case typeSingleton: Type.Singleton => typeSingletonTraverser.traverse(typeSingleton)
    case _ => emitComment(s"UNSUPPORTED: $typeRef")
  }
}

object TypeRefTraverser extends TypeRefTraverserImpl(
  TypeNameTraverser,
  TypeSelectTraverser,
  TypeProjectTraverser,
  TypeSingletonTraverser
)