package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.While

trait WhileTraverser extends ScalaTreeTraverser1[While]

private[traversers] class WhileTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser,
                                             blockWrappingTermTraverser: => BlockWrappingTermTraverser) extends WhileTraverser {

  override def traverse(`while`: While): While = {
    val traversedExpr = expressionTermTraverser.traverse(`while`.expr)
    val traversedBody = blockWrappingTermTraverser.traverse(`while`.body).block
    While(traversedExpr, traversedBody)
  }
}
