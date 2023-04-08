package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Assign

trait AssignTraverser extends ScalaTreeTraverser[Assign] {

  def traverse(assign: Assign): Unit
}

private[traversers] class AssignTraverserImpl(assignLHSTraverser: => AssignLHSTraverser,
                                              expressionTermTraverser: => TermTraverser) extends AssignTraverser {

  // This traverser handles a 'var' assignment only.
  // The other two cases of a named argument in an annotation or a method invocation - are handled by a separate traverser
  override def traverse(assign: Assign): Unit = {
    assignLHSTraverser.traverse(assign.lhs)
    expressionTermTraverser.traverse(assign.rhs)
  }
}
