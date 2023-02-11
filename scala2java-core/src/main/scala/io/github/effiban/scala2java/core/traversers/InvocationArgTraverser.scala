package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term
import scala.meta.Term.Assign

private[traversers] class InvocationArgTraverser(assignLHSTraverser: => AssignLHSTraverser,
                                                 expressionTraverser: => ExpressionTraverser) extends ArgumentTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = {
    arg match {
      case assign: Assign =>
        assignLHSTraverser.traverse(assign.lhs, context.argNameAsComment)
        traverse(assign.rhs, context)
      case term => expressionTraverser.traverse(term)
    }
  }
}
