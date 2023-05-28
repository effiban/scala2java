package io.github.effiban.scala2java.core.renderers

import scala.meta.Term.ApplyUnary

trait ApplyUnaryRenderer extends JavaTreeRenderer[ApplyUnary]

private[renderers] class ApplyUnaryRendererImpl(termNameRenderer: TermNameRenderer,
                                                expressionTermRenderer: => ExpressionTermRenderer) extends ApplyUnaryRenderer {

  override def render(applyUnary: ApplyUnary): Unit = {
    termNameRenderer.render(applyUnary.op)
    expressionTermRenderer.render(applyUnary.arg)
  }
}
