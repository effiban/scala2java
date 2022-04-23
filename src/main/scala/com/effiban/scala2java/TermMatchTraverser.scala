package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term

object TermMatchTraverser extends ScalaTreeTraverser[Term.Match] {

  def traverse(termMatch: Term.Match): Unit = {
    // TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    GenericTreeTraverser.traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(GenericTreeTraverser.traverse)
    emitBlockEnd()
  }
}
