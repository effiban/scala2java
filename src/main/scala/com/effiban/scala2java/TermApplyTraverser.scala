package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitParametersEnd, emitParametersStart}

import scala.meta.Term

object TermApplyTraverser extends ScalaTreeTraverser[Term.Apply] {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    GenericTreeTraverser.traverse(termApply.fun)
    emit(" ")
    emitParametersStart()
    GenericTreeTraverser.traverse(termApply.args)
    emitParametersEnd()
  }
}
