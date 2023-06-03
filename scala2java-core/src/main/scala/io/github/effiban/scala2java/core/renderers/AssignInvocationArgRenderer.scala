package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term.Assign

private[renderers] class AssignInvocationArgRenderer(assignLHSRenderer: => AssignLHSRenderer,
                                                     expressionTermRenderer: ExpressionTermRenderer)
  extends InvocationArgRenderer[Assign] {

  override def render(assign: Assign, context: ArgumentContext): Unit = {
    assignLHSRenderer.render(assign.lhs, context.argNameAsComment)
    expressionTermRenderer.render(assign.rhs)
  }
}
