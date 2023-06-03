package io.github.effiban.scala2java.core.renderers

import scala.meta.Term
import scala.meta.Term.If

/** Renderer for terms appearing in the context of an evaluated expression, such as:
 *   - RHS of an assigment
 *   - method argument
 *   - return value
 */

trait ExpressionTermRenderer extends TermRenderer

private[renderers] class ExpressionTermRendererImpl(expressionTermRefRenderer: => ExpressionTermRefRenderer,
                                                    ifRenderer: => IfRenderer,
                                                    defaultTermRenderer: => DefaultTermRenderer) extends ExpressionTermRenderer {

  override def render(expression: Term): Unit = {
    expression match {
      case ref: Term.Ref => expressionTermRefRenderer.render(ref)
      case `if`: If => ifRenderer.renderAsTertiaryOp(`if`)
      case aTerm => defaultTermRenderer.render(aTerm)
    }
  }
}
