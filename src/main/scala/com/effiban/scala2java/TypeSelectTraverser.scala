package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

object TypeSelectTraverser extends ScalaTreeTraverser[Type.Select] {

  // A scala type selecting expression like: a.B
  def traverse(typeSelect: Type.Select): Unit = {
    TermRefTraverser.traverse(typeSelect.qual)
    emit(".")
    TypeNameTraverser.traverse(typeSelect.name)
  }
}
