package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.TermSelectContext
import io.github.effiban.scala2java.core.entities.EnclosingDelimiter.Parentheses
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait ExpressionTermSelectRenderer {
  def render(termSelect: Term.Select, context: TermSelectContext = TermSelectContext()): Unit
}

private[renderers] class ExpressionTermSelectRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                                          typeListRenderer: => TypeListRenderer,
                                                          termNameRenderer: TermNameRenderer)
                                                         (implicit javaWriter: JavaWriter) extends ExpressionTermSelectRenderer {

  import javaWriter._

  // qualified name in the context of an evaluated expression
  override def render(select: Term.Select, context: TermSelectContext = TermSelectContext()): Unit = {
    renderQualifier(select.qual)
    renderQualifierSeparator(select.qual)
    typeListRenderer.render(context.appliedTypeArgs)
    termNameRenderer.render(select.name)
  }

  private def renderQualifier(qualifier: Term): Unit = {
    qualifier match {
      case qual@(_: Term.Function | Term.Ascribe(_: Term.Function, _)) => renderInsideParens(qual)
      case qual => expressionTermRenderer.render(qual)
    }
  }

  private def renderInsideParens(qual: Term): Unit = {
    writeArgumentsStart(Parentheses)
    expressionTermRenderer.render(qual)
    writeArgumentsEnd(Parentheses)
  }

  private def renderQualifierSeparator(qualifier: Term): Unit = {
    qualifier match {
      case _: Term.Apply => writeLine()
      case _ =>
    }
    writeQualifierSeparator()
  }
}
