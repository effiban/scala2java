package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term
import scala.meta.Term.TryWithHandler

object TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler] {

  def traverse(tryWithHandler: TryWithHandler): Unit = {
    emit("try ")
    GenericTreeTraverser.traverse(tryWithHandler.expr)
    traverseCatchClause(tryWithHandler.catchp)
    tryWithHandler.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      GenericTreeTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(handler: Term): Unit = {
    emit("catch (")
    GenericTreeTraverser.traverse(handler)
    emit(")")
  }
}
