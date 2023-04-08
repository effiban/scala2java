package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

/** Traverser for terms which are function expressions (the part without the arguments) */
trait FunTermTraverser extends ScalaTreeTraverser[Term]

private[traversers] class FunTermTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends FunTermTraverser {

  override def traverse(fun: Term): Unit = {
    // TODO handle special cases to prevent infinite recursion of desugaring
    expressionTermTraverser.traverse(fun)
  }
}
