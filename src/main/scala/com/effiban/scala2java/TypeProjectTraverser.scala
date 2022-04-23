package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeProjectTraverser extends ScalaTreeTraverser[Type.Project] {

  // A scala type projecting expression like: a#B
  def traverse(typeProject: Type.Project): Unit = {
    TypeTraverser.traverse(typeProject.qual)
    emit(".")
    TypeNameTraverser.traverse(typeProject.name)
  }
}
