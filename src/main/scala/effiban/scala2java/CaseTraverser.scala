package effiban.scala2java

import scala.meta.Case

trait CaseTraverser extends ScalaTreeTraverser[Case]

private[scala2java] class CaseTraverserImpl(patTraverser: => PatTraverser,
                                            termTraverser: => TermTraverser)
                                           (implicit javaEmitter: JavaEmitter) extends CaseTraverser {

  import javaEmitter._

  def traverse(`case`: Case): Unit = {
    emit("case ")
    patTraverser.traverse(`case`.pat)
    `case`.cond.foreach(cond => {
      emit(" && ")
      emitStartDelimiter(Parentheses)
      termTraverser.traverse(cond)
      emitEndDelimiter(Parentheses)
    })
    emitArrow()
    termTraverser.traverse(`case`.body)
    emitStatementEnd()
  }
}

object CaseTraverser extends CaseTraverserImpl(PatTraverser, TermTraverser)
