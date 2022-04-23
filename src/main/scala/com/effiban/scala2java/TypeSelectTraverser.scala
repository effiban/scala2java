package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeSelectTraverser extends ScalaTreeTraverser[Type.Select] {

  // A scala type selecting expression like: a.B
  def traverse(typeSelect: Type.Select): Unit = {
    GenericTreeTraverser.traverse(typeSelect.qual)
    emit(".")
    GenericTreeTraverser.traverse(typeSelect.name)
  }
}
