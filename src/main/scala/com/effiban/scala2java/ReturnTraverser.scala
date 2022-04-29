package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser[Return]

object ReturnTraverser extends ReturnTraverser {

  override def traverse(`return`: Return): Unit = {
    emit("return ")
    TermTraverser.traverse(`return`.expr)
  }
}
