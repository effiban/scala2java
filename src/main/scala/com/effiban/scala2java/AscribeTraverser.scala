package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser[Ascribe]

object AscribeTraverser extends AscribeTraverser {

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting. e.g. x = (short)2
  override def traverse(ascribe: Ascribe): Unit = {
    emit("(")
    TypeTraverser.traverse(ascribe.tpe)
    emit(")")
    TermTraverser.traverse(ascribe.expr)
  }
}
