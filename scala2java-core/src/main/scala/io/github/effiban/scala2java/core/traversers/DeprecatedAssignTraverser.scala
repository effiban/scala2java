package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Assign

@deprecated
trait DeprecatedAssignTraverser extends ScalaTreeTraverser[Assign] {

  def traverse(assign: Assign): Unit
}

@deprecated
private[traversers] class DeprecatedAssignTraverserImpl(assignLHSTraverser: => DeprecatedAssignLHSTraverser,
                                                        expressionTermTraverser: => DeprecatedExpressionTermTraverser) extends DeprecatedAssignTraverser {

  // This traverser handles a 'var' assignment only.
  // The other two cases of a named argument in an annotation or a method invocation - are handled by a separate traverser
  override def traverse(assign: Assign): Unit = {
    assignLHSTraverser.traverse(assign.lhs)
    expressionTermTraverser.traverse(assign.rhs)
  }
}
