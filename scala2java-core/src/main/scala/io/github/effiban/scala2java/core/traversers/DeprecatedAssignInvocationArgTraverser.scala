package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term.Assign

@deprecated
private[traversers] class DeprecatedAssignInvocationArgTraverser(assignLHSTraverser: => DeprecatedAssignLHSTraverser,
                                                                 expressionTermTraverser: DeprecatedExpressionTermTraverser) extends DeprecatedInvocationArgTraverser[Assign] {

  override def traverse(assign: Assign, context: ArgumentContext): Unit = {
    assignLHSTraverser.traverse(assign.lhs, context.argNameAsComment)
    expressionTermTraverser.traverse(assign.rhs)
  }
}
