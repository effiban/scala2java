package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.While

trait WhileTraverser extends ScalaTreeTraverser[While]

object WhileTraverser extends WhileTraverser {

  override def traverse(`while`: While): Unit = {
    emit("while (")
    TermTraverser.traverse(`while`.expr)
    emit(") ")
    TermTraverser.traverse(`while`.body)
  }
}
