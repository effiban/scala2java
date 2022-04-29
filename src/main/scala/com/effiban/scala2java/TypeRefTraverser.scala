package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeRefTraverser extends ScalaTreeTraverser[Type.Ref]

object TypeRefTraverser extends TypeRefTraverser {

  override def traverse(typeRef: Type.Ref): Unit = typeRef match {
    case typeName: Type.Name => TypeNameTraverser.traverse(typeName)
    case typeSelect: Type.Select => TypeSelectTraverser.traverse(typeSelect)
    case typeProject: Type.Project => TypeProjectTraverser.traverse(typeProject)
    case typeSingleton: Type.Singleton => TypeSingletonTraverser.traverse(typeSingleton)
    case _ => emitComment(s"UNSUPPORTED: $typeRef")
  }
}
