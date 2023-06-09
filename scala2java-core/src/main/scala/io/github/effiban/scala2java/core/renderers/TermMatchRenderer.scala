package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermMatchRenderer extends JavaTreeRenderer[Term.Match]

private[renderers] class TermMatchRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                               caseRenderer: => CaseRenderer)
                                              (implicit javaWriter: JavaWriter) extends TermMatchRenderer {

  import javaWriter._

  override def render(termMatch: Term.Match): Unit = {
    //TODO handle mods (what is this in a 'match'?...)
    write("switch ")
    write("(")
    expressionTermRenderer.render(termMatch.expr)
    write(")")
    writeBlockStart()
    termMatch.cases.foreach(caseRenderer.render)
    writeBlockEnd()
  }
}
