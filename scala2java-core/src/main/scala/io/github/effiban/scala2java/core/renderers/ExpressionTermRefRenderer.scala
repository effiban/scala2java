package io.github.effiban.scala2java.core.renderers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefRenderer extends TermRefRenderer

private[renderers] class ExpressionTermRefRendererImpl(expressionTermSelectRenderer: => ExpressionTermSelectRenderer,
                                                       applyUnaryRenderer: => ApplyUnaryRenderer,
                                                       defaultTermRefRenderer: => DefaultTermRefRenderer)
  extends ExpressionTermRefRenderer {

  override def render(termRef: Term.Ref): Unit = termRef match {
    case termSelect: Term.Select => expressionTermSelectRenderer.render(termSelect)
    case applyUnary: ApplyUnary => applyUnaryRenderer.render(applyUnary)
    case aTermRef => defaultTermRefRenderer.render(aTermRef)
  }
}
