package io.github.effiban.scala2java.core.renderers

import scala.meta.Term

trait StatTermRenderer extends JavaTreeRenderer[Term]

private[renderers] class StatTermRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
                                              defaultTermRenderer: => DefaultTermRenderer) extends StatTermRenderer {
  override def render(term: Term): Unit = term match {
    case ref: Term.Ref => expressionTermRefRenderer.render(ref)
    case aTerm => defaultTermRenderer.render(aTerm)
  }
}
