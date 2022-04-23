package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.New

object NewTraverser extends ScalaTreeTraverser[New] {

  override def traverse(`new`: New): Unit = {
    emit("new ")
    GenericTreeTraverser.traverse(`new`.init)
  }
}
