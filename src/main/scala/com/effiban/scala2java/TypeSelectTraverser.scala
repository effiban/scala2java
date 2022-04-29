package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Type

trait TypeSelectTraverser extends ScalaTreeTraverser[Type.Select]

object TypeSelectTraverser extends TypeSelectTraverser {

  // A scala type selecting expression like: a.B
  override def traverse(typeSelect: Type.Select): Unit = {
    TermRefTraverser.traverse(typeSelect.qual)
    emit(".")
    TypeNameTraverser.traverse(typeSelect.name)
  }
}
