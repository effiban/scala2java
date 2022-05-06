package com.effiban.scala2java

import scala.meta.Term.While

trait WhileTraverser extends ScalaTreeTraverser[While]

private[scala2java] class WhileTraverserImpl(termTraverser: => TermTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends WhileTraverser {

  import javaEmitter._

  override def traverse(`while`: While): Unit = {
    emit("while (")
    termTraverser.traverse(`while`.expr)
    emit(") ")
    termTraverser.traverse(`while`.body)
  }
}

object WhileTraverser extends WhileTraverserImpl(TermTraverser)