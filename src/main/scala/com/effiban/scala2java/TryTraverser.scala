package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.{Case, Term}

trait TryTraverser extends ScalaTreeTraverser[Term.Try]

private[scala2java] class TryTraverserImpl(termTraverser: => TermTraverser,
                                           patTraverser: => PatTraverser) extends TryTraverser {

  override def traverse(`try`: Term.Try): Unit = {
    emit("try ")
    termTraverser.traverse(`try`.expr)
    `try`.catchp.foreach(traverseCatchClause)
    `try`.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      termTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(`case`: Case): Unit = {
    emit("catch (")
    patTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      termTraverser.traverse(cond)
      emit(")")
    })
    emit(")")
    emitBlockStart()
    termTraverser.traverse(`case`.body)
    emitBlockEnd()
  }
}

object TryTraverser extends TryTraverserImpl(TermTraverser, PatTraverser)
