package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatTypedTraverser extends ScalaTreeTraverser1[Pat.Typed]

private[traversers] class PatTypedTraverserImpl(patTraverser: => PatTraverser,
                                                typeTraverser: => TypeTraverser) extends PatTypedTraverser {

  // Typed pattern expression, e.g. a: Int (in lhs of case clause)
  override def traverse(typedPattern: Pat.Typed): Pat.Typed = {
    val traversedLhs = patTraverser.traverse(typedPattern.lhs)
    val traversedRhs = typeTraverser.traverse(typedPattern.rhs)
    Pat.Typed(traversedLhs, traversedRhs)
  }
}
