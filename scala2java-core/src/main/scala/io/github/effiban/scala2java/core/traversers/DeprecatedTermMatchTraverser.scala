package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

@deprecated
trait DeprecatedTermMatchTraverser extends ScalaTreeTraverser[Term.Match]

@deprecated
private[traversers] class DeprecatedTermMatchTraverserImpl(expressionTermTraverser: => DeprecatedExpressionTermTraverser,
                                                           caseTraverser: => DeprecatedCaseTraverser)
                                                          (implicit javaWriter: JavaWriter) extends DeprecatedTermMatchTraverser {

  import javaWriter._

  override def traverse(termMatch: Term.Match): Unit = {
    //TODO handle mods (what is this in a 'match'?...)
    write("switch ")
    write("(")
    expressionTermTraverser.traverse(termMatch.expr)
    write(")")
    writeBlockStart()
    termMatch.cases.foreach(caseTraverser.traverse)
    writeBlockEnd()
  }
}
