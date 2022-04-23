package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.{Case, Term}

object TryTraverser extends ScalaTreeTraverser[Term.Try] {

  def traverse(`try`: Term.Try): Unit = {
    emit("try ")
    GenericTreeTraverser.traverse(`try`.expr)
    `try`.catchp.foreach(traverseCatchClause)
    `try`.finallyp.foreach(finallyp => {
      emit("finally")
      emitBlockStart()
      GenericTreeTraverser.traverse(finallyp)
      emitBlockEnd()
    })
  }

  private def traverseCatchClause(`case`: Case): Unit = {
    emit("catch (")
    GenericTreeTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      GenericTreeTraverser.traverse(cond)
      emit(")")
    })
    emit(")")
    emitBlockStart()
    GenericTreeTraverser.traverse(`case`.body)
    emitBlockEnd()
  }
}
