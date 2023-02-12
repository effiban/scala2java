package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.classifiers.InvocationArgClassifier
import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.core.entities.ArgumentCoordinates

import scala.meta.Term

private[traversers] class DefaultInvocationArgTraverser(expressionTraverser: => ExpressionTraverser,
                                                        invocationArgClassifier: InvocationArgClassifier) extends InvocationArgTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = {
    import context._
    val maybeCoords = maybeParent.map(parent => ArgumentCoordinates(parent, maybeName, index))
    val adjustedArg = maybeCoords match {
      case Some(coords) if invocationArgClassifier.isPassedByName(coords) => Term.Function(Nil, arg)
      case _ => arg
    }
    expressionTraverser.traverse(adjustedArg)
  }
}
