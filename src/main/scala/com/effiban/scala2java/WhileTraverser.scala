package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.While

object WhileTraverser extends ScalaTreeTraverser[While] {
  def traverse(`while`: While): Unit = {
    emit("while (")
    GenericTreeTraverser.traverse(`while`.expr)
    emit(") ")
    GenericTreeTraverser.traverse(`while`.body)
  }
}
