package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Throw

trait ThrowTraverser extends ScalaTreeTraverser1[Throw]

private[traversers] class ThrowTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends ThrowTraverser {

  override def traverse(`throw`: Throw): Throw = {
    `throw`.copy(expr = expressionTermTraverser.traverse(`throw`.expr))
  }
}
