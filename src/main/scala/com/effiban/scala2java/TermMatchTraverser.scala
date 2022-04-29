package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term

trait TermMatchTraverser extends ScalaTreeTraverser[Term.Match]

object TermMatchTraverser extends TermMatchTraverser {

  override def traverse(termMatch: Term.Match): Unit = {
    // TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    TermTraverser.traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(CaseTraverser.traverse)
    emitBlockEnd()
  }
}
