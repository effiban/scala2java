package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.{Case, Term}

trait TryTraverser extends ScalaTreeTraverser[Term.Try]

object TryTraverser extends TryTraverser {

  override def traverse(`try`: Term.Try): Unit = {
    emit("try ")
    TermTraverser.traverse(`try`.expr)
    `try`.catchp.foreach(traverseCatchClause)
    `try`.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      TermTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(`case`: Case): Unit = {
    emit("catch (")
    PatTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      TermTraverser.traverse(cond)
      emit(")")
    })
    emit(")")
    emitBlockStart()
    TermTraverser.traverse(`case`.body)
    emitBlockEnd()
  }
}
