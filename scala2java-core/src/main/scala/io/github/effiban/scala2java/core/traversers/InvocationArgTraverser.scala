package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.entities.Decision.Uncertain

import scala.meta.Term
import scala.meta.Term.{Assign, Block}

private[traversers] class InvocationArgTraverser(assignTraverser: => AssignTraverser,
                                                 termFunctionTraverser: => TermFunctionTraverser,
                                                 expressionTraverser: => ExpressionTraverser) extends ArgumentTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = arg match {
    case assign: Assign => assignTraverser.traverse(assign = assign, lhsAsComment = context.argNameAsComment)
    // A block cannot be passed as an argument in Java, so wrapping it in a Lambda
    case block: Block => termFunctionTraverser.traverse(Term.Function(Nil, block), shouldBodyReturnValue = Uncertain)
    case term => expressionTraverser.traverse(term)
  }
}
