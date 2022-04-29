package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.New

trait NewTraverser extends ScalaTreeTraverser[New]

object NewTraverser extends NewTraverser {

  override def traverse(`new`: New): Unit = {
    emit("new ")
    InitTraverser.traverse(`new`.init)
  }
}
