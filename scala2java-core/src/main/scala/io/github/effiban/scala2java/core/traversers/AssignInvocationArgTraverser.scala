package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term
import scala.meta.Term.Assign

private[traversers] class AssignInvocationArgTraverser(assignLHSTraverser: => AssignLHSTraverser,
                                                       defaultInvocationArgTraverser: ArgumentTraverser[Term]) extends InvocationArgTraverser[Assign] {

  override def traverse(assign: Assign, context: ArgumentContext): Unit = {
    assignLHSTraverser.traverse(assign.lhs, context.argNameAsComment)
    val adjustedContext = assign.lhs match {
      case name: Term.Name => context.copy(maybeName = Some(name))
      // As far as I know, a LHS must always be a Term.Name, so this is here just in case but I don't know how to test it
      case _ => context
    }
    defaultInvocationArgTraverser.traverse(assign.rhs, adjustedContext)
  }
}
