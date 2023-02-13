package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.ArgumentContext
import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates
import io.github.effiban.scala2java.spi.predicates.InvocationArgByNamePredicate

import scala.meta.Term

private[traversers] class DefaultInvocationArgTraverser(expressionTraverser: => ExpressionTraverser,
                                                        invocationArgByNamePredicate: InvocationArgByNamePredicate)
  extends InvocationArgTraverser[Term] {

  override def traverse(arg: Term, context: ArgumentContext): Unit = {
    import context._
    val maybeCoords = maybeParent.map(parent => InvocationArgCoordinates(parent, maybeName, index))
    val adjustedArg = maybeCoords match {
      case Some(coords) if invocationArgByNamePredicate(coords) => Term.Function(Nil, arg)
      case _ => arg
    }
    expressionTraverser.traverse(adjustedArg)
  }
}
