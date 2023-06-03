package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term
import scala.meta.Term.Assign

class CompositeInvocationArgRenderer(assignInvocationArgRenderer: InvocationArgRenderer[Term.Assign],
                                     expressionTermRenderer: ExpressionTermRenderer) extends InvocationArgRenderer[Term] {

    override def render(arg: Term, context: ArgumentContext): Unit = {
      arg match {
        case assign: Assign => assignInvocationArgRenderer.render(assign, context)
        case term => expressionTermRenderer.render(term)
      }
    }
}
