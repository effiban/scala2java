package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser1[Assign]

private[traversers] class AssignTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends AssignTraverser {

  override def traverse(assign: Assign): Assign = {
    val traversedLhs = expressionTermTraverser.traverse(assign.lhs)
    val traversedRhs = expressionTermTraverser.traverse(assign.rhs)
    Assign(traversedLhs, traversedRhs)
  }
}
