package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.InvocationArgClassifier
import io.github.effiban.scala2java.core.contexts.ArgumentContext

import scala.meta.Term

private[traversers] class DefaultInvocationArgTraverser(expressionTraverser: => ExpressionTraverser,
                                                        invocationArgClassifier: InvocationArgClassifier) extends InvocationArgTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = {
    val adjustedArg = if (invocationArgClassifier.isPassedByName(context)) Term.Function(Nil, arg) else arg
    expressionTraverser.traverse(adjustedArg)
  }
}
