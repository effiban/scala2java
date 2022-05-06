package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitArrow, emitStatementEnd}

import scala.meta.Case

trait CaseTraverser extends ScalaTreeTraverser[Case]

private[scala2java] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            termTraverser: => TermTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends CaseTraverser {

  def traverse(`case`: Case): Unit = {
    emit("case ")
    patTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && (")
      termTraverser.traverse(cond)
      emit(")")
    })
    emitArrow()
    termTraverser.traverse(`case`.body)
    emitStatementEnd()
  }
}

object CaseTraverser extends CaseTraverserImpl(PatTraverser, TermTraverser)
