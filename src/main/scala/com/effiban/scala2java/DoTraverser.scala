package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Do

object DoTraverser extends ScalaTreeTraverser[Do] {

  override def traverse(`do`: Do): Unit = {
    emit("do ")
    TermTraverser.traverse(`do`.body)
    emit("while (")
    TermTraverser.traverse(`do`.expr)
    emit(")")
  }
}
