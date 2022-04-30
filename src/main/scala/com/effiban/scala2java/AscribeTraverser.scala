package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Ascribe

trait AscribeTraverser extends ScalaTreeTraverser[Ascribe]

private[scala2java] class AscribeTraverserImpl(typeTraverser: => TypeTraverser,
                                               termTraverser: => TermTraverser) extends AscribeTraverser {

  // Explicitly specified type, e.g.: x = 2:Short
  // Java equivalent is casting. e.g. x = (short)2
  override def traverse(ascribe: Ascribe): Unit = {
    emit("(")
    typeTraverser.traverse(ascribe.tpe)
    emit(")")
    termTraverser.traverse(ascribe.expr)
  }
}

object AscribeTraverser extends AscribeTraverserImpl(TypeTraverser, TermTraverser)
