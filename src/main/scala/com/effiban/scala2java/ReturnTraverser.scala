package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Return

object ReturnTraverser extends ScalaTreeTraverser[Return] {

  override def traverse(`return`: Return): Unit = {
    emit("return ")
    GenericTreeTraverser.traverse(`return`.expr)
  }
}
