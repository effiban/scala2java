package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.While

trait WhileTraverser extends ScalaTreeTraverser[While]

private[scala2java] class WhileTraverserImpl(termTraverser: => TermTraverser) extends WhileTraverser {

  override def traverse(`while`: While): Unit = {
    emit("while (")
    termTraverser.traverse(`while`.expr)
    emit(") ")
    termTraverser.traverse(`while`.body)
  }
}

object WhileTraverser extends WhileTraverserImpl(TermTraverser)