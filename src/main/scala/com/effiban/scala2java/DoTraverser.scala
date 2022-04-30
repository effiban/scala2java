package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Do

trait DoTraverser extends ScalaTreeTraverser[Do]

private[scala2java] class DoTraverserImpl(termTraverser: => TermTraverser) extends DoTraverser {

  override def traverse(`do`: Do): Unit = {
    emit("do ")
    termTraverser.traverse(`do`.body)
    emit("while (")
    termTraverser.traverse(`do`.expr)
    emit(")")
  }
}

object DoTraverser extends DoTraverserImpl(TermTraverser)
