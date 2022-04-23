package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow, emitStatementEnd}

import scala.meta.Case

object CaseTraverser extends ScalaTreeTraverser[Case] {

  def traverse(`case`: Case): Unit = {
    emit("case ")
    GenericTreeTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      GenericTreeTraverser.traverse(cond)
      emit(")")
    })
    emitArrow()
    GenericTreeTraverser.traverse(`case`.body)
    emitStatementEnd()
  }
}
