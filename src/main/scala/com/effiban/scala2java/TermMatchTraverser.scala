package com.effiban.scala2java

import scala.meta.Term

trait TermMatchTraverser extends ScalaTreeTraverser[Term.Match]

private[scala2java] class TermMatchTraverserImpl(termTraverser: => TermTraverser,
                                                 caseTraverser: => CaseTraverser)
                                                (implicit javaEmitter: JavaEmitter) extends TermMatchTraverser {
  import javaEmitter._

  override def traverse(termMatch: Term.Match): Unit = {
    //TODO handle mods (what is this in a 'match'?...)
    emit("switch ")
    emit("(")
    termTraverser.traverse(termMatch.expr)
    emit(")")
    emitBlockStart()
    termMatch.cases.foreach(caseTraverser.traverse)
    emitBlockEnd()
  }
}

object TermMatchTraverser extends TermMatchTraverserImpl(TermTraverser, CaseTraverser)
