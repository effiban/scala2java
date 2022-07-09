package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

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

object TermMatchTraverser extends TermMatchTraverserImpl(TermTraverser, CaseTraverser)
