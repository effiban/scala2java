package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term
import scala.meta.Term.TryWithHandler

trait TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler]

object TryWithHandlerTraverser extends TryWithHandlerTraverser {

  override def traverse(tryWithHandler: TryWithHandler): Unit = {
    emit("try ")
    TermTraverser.traverse(tryWithHandler.expr)
    traverseCatchClause(tryWithHandler.catchp)
    tryWithHandler.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      TermTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(handler: Term): Unit = {
    emit("catch (")
    TermTraverser.traverse(handler)
    emit(")")
  }
}
