package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser[Return]

private[scala2java] class ReturnTraverserImpl(termTraverser: => TermTraverser) extends ReturnTraverser {

  override def traverse(`return`: Return): Unit = {
    emit("return ")
    termTraverser.traverse(`return`.expr)
  }
}

object ReturnTraverser extends ReturnTraverserImpl(TermTraverser)
