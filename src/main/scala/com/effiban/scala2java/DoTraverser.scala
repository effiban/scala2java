package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Do

trait DoTraverser extends ScalaTreeTraverser[Do]

object DoTraverser extends DoTraverser {

  override def traverse(`do`: Do): Unit = {
    emit("do ")
    TermTraverser.traverse(`do`.body)
    emit("while (")
    TermTraverser.traverse(`do`.expr)
    emit(")")
  }
}
