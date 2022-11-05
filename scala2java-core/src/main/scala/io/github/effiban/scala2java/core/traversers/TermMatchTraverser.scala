package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermMatchTraverser extends ScalaTreeTraverser[Term.Match]

private[traversers] class TermMatchTraverserImpl(termTraverser: => TermTraverser,
                                                 caseTraverser: => CaseTraverser)
                                                (implicit javaWriter: JavaWriter) extends TermMatchTraverser {

  import javaWriter._

  override def traverse(termMatch: Term.Match): Unit = {
    //TODO handle mods (what is this in a 'match'?...)
    write("switch ")
    write("(")
    termTraverser.traverse(termMatch.expr)
    write(")")
    writeBlockStart()
    termMatch.cases.foreach(caseTraverser.traverse)
    writeBlockEnd()
  }
}
