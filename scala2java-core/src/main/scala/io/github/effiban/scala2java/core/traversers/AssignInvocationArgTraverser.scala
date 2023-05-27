package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term.Assign

private[traversers] class AssignInvocationArgTraverser(assignLHSTraverser: => AssignLHSTraverser,
                                                       expressionTermTraverser: ExpressionTermTraverser) extends InvocationArgTraverser[Assign] {

  override def traverse(assign: Assign, context: ArgumentContext): Unit = {
    assignLHSTraverser.traverse(assign.lhs, context.argNameAsComment)
    expressionTermTraverser.traverse(assign.rhs)
  }
}
