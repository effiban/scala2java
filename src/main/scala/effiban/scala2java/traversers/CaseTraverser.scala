package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.writers.JavaWriter

import scala.meta.Case

trait CaseTraverser extends ScalaTreeTraverser[Case]

private[traversers] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            termTraverser: => TermTraverser)
                                           (implicit javaWriter: JavaWriter) extends CaseTraverser {

  import javaWriter._

  def traverse(`case`: Case): Unit = {
    write("case ")
    patTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      write(" && ")
      writeStartDelimiter(Parentheses)
      termTraverser.traverse(cond)
      writeEndDelimiter(Parentheses)
    })
    writeArrow()
    termTraverser.traverse(`case`.body)
    writeStatementEnd()
  }
}

object CaseTraverser extends CaseTraverserImpl(PatTraverser, TermTraverser)
