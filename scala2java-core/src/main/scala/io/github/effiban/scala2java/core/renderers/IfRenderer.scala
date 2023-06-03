package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, IfRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.{Block, If}
import scala.meta.{Lit, Term}

trait IfRenderer {
  def render(`if`: If, context: IfRenderContext = IfRenderContext()): Unit

  def renderAsTertiaryOp(`if`: If): Unit
}

private[renderers] class IfRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                        blockRenderer: => BlockRenderer,
                                        defaultTermRenderer: => DefaultTermRenderer)
                                       (implicit javaWriter: JavaWriter) extends IfRenderer {

  import javaWriter._

  override def render(`if`: If, context: IfRenderContext = IfRenderContext()): Unit = {
    //TODO handle mods (what do they represent in an 'if'?...)
    write("if (")
    expressionTermRenderer.render(`if`.cond)
    write(")")
    renderNonExpressionClause(`if`.thenp, context)
    `if`.elsep match {
      case Lit.Unit() =>
      case elsep =>
        write("else")
        renderNonExpressionClause(elsep, context)
    }
  }

  override def renderAsTertiaryOp(`if`: If): Unit = {
    write("(")
    expressionTermRenderer.render(`if`.cond)
    write(") ? ")
    expressionTermRenderer.render(`if`.thenp)
    write(" : ")
    `if`.elsep match {
      case Lit.Unit() => throw new IllegalStateException("Trying to render as a tertiary op with no 'else' clause")
      case elsep => expressionTermRenderer.render(elsep)
    }
  }

  private def renderNonExpressionClause(clause: Term, ifRenderContext: IfRenderContext): Unit = {
    clause match {
      case block: Block => blockRenderer.render(block, BlockRenderContext(ifRenderContext.uncertainReturn))
      case term =>
        write(" ")
        defaultTermRenderer.render(term)
    }
  }
}
