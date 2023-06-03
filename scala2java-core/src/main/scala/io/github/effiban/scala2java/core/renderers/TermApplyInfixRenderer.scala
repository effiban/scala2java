package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TermApplyInfixRenderer extends JavaTreeRenderer[Term.ApplyInfix]

private[renderers] class TermApplyInfixRendererImpl(expressionTermRenderer: => ExpressionTermRenderer,
                                                    termNameRenderer: TermNameRenderer)
                                                   (implicit javaWriter: JavaWriter) extends TermApplyInfixRenderer {

  import javaWriter._

  override def render(termApplyInfix: Term.ApplyInfix): Unit = {
    //TODO handle type args
    termApplyInfix.args match {
      case Nil => throw new IllegalStateException("An Term.ApplyInfix must have at least one RHS arg")
      case arg :: Nil =>
        expressionTermRenderer.render(termApplyInfix.lhs)
        write(" ")
        termNameRenderer.render(termApplyInfix.op)
        write(" ")
        expressionTermRenderer.render(arg)
      case _ =>
        // If the infix has multiple RHS args - there is no Java equivalent
        write(s"UNSUPPORTED: $termApplyInfix")
    }
  }
}
