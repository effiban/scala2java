package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.New

trait NewTraverser extends ScalaTreeTraverser[New]

private[scala2java] class NewTraverserImpl(initTraverser: => InitTraverser) extends NewTraverser {

  override def traverse(`new`: New): Unit = {
    emit("new ")
    initTraverser.traverse(`new`.init)
  }
}

object NewTraverser extends NewTraverserImpl(InitTraverser)
