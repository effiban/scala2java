package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow, emitStatementEnd}

import scala.meta.Case

trait CaseTraverser extends ScalaTreeTraverser[Case]

object CaseTraverser extends CaseTraverser {

  def traverse(`case`: Case): Unit = {
    emit("case ")
    PatTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      TermTraverser.traverse(cond)
      emit(")")
    })
    emitArrow()
    TermTraverser.traverse(`case`.body)
    emitStatementEnd()
  }
}
