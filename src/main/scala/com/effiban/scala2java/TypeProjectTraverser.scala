package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeProjectTraverser extends ScalaTreeTraverser[Type.Project]

object TypeProjectTraverser extends TypeProjectTraverser {

  // A scala type projecting expression like: a#B
  override def traverse(typeProject: Type.Project): Unit = {
    TypeTraverser.traverse(typeProject.qual)
    emit(".")
    TypeNameTraverser.traverse(typeProject.name)
  }
}
