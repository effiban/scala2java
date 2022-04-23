package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Do

object DoTraverser extends ScalaTreeTraverser[Do] {

  override def traverse(`do`: Do): Unit = {
    emit("do ")
    GenericTreeTraverser.traverse(`do`.body)
    emit("while (")
    GenericTreeTraverser.traverse(`do`.expr)
    emit(")")
  }
}
