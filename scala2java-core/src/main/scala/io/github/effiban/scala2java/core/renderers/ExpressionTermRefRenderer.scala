package io.github.effiban.scala2java.core.renderers

import scala.meta.Term
import scala.meta.Term.ApplyUnary

trait ExpressionTermRefRenderer extends TermRefRenderer

private[renderers] class ExpressionTermRefRendererImpl(defaultTermRefRenderer: => DefaultTermRefRenderer)
  extends ExpressionTermRefRenderer {

  override def render(termRef: Term.Ref): Unit = termRef match {
    case termSelect: Term.Select => // TODO
    case applyUnary: ApplyUnary => // TODO
    case aTermRef => defaultTermRefRenderer.render(aTermRef)
  }
}
