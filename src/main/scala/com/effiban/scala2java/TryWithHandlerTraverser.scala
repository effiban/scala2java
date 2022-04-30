package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term
import scala.meta.Term.TryWithHandler

trait TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler]

private[scala2java] class TryWithHandlerTraverserImpl(termTraverser: => TermTraverser) extends TryWithHandlerTraverser {

  override def traverse(tryWithHandler: TryWithHandler): Unit = {
    emit("try ")
    termTraverser.traverse(tryWithHandler.expr)
    traverseCatchClause(tryWithHandler.catchp)
    tryWithHandler.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      termTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(handler: Term): Unit = {
    emit("catch (")
    termTraverser.traverse(handler)
    emit(")")
  }
}

object TryWithHandlerTraverser extends TryWithHandlerTraverserImpl(TermTraverser)
