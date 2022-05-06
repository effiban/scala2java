package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term

trait TermMatchTraverser extends ScalaTreeTraverser[Term.Match]

private[scala2java] class TermMatchTraverserImpl(termTraverser: => TermTraverser)
                                                (implicit javaEmitter: JavaEmitter) extends TermMatchTraverser {

  override def traverse(termMatch: Term.Match): Unit = {
    // TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    termTraverser.traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(CaseTraverser.traverse)
    emitBlockEnd()
  }
}

object TermMatchTraverser extends TermMatchTraverserImpl(TermTraverser)
