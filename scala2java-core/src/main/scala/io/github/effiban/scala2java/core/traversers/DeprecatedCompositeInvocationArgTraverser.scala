package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term
import scala.meta.Term.Assign

@deprecated
private[traversers] class DeprecatedCompositeInvocationArgTraverser(assignInvocationArgTraverser: => DeprecatedArgumentTraverser[Assign],
                                                                    expressionTermTraverser: => DeprecatedExpressionTermTraverser)
  extends DeprecatedInvocationArgTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = {
    arg match {
      case assign: Assign => assignInvocationArgTraverser.traverse(assign, context)
      case term => expressionTermTraverser.traverse(term)
    }
  }
}
