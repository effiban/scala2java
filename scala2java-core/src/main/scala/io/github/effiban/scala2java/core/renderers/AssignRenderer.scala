package io.github.effiban.scala2java.core.renderers

import scala.meta.Term.Assign

trait AssignRenderer extends JavaTreeRenderer[Assign]

private[renderers] class AssignRendererImpl(assignLHSRenderer: => AssignLHSRenderer,
                                            expressionTermRenderer: => ExpressionTermRenderer) extends AssignRenderer {

  // This renderer handles a 'var' assignment only.
  // The other two cases of a named argument in an annotation or a method invocation - are handled by a separate renderer
  override def render(assign: Assign): Unit = {
    assignLHSRenderer.render(assign.lhs)
    expressionTermRenderer.render(assign.rhs)
  }
}
