package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Do

trait DoTraverser extends ScalaTreeTraverser1[Do]

private[traversers] class DoTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser,
                                          expressionTermTraverser: => ExpressionTermTraverser) extends DoTraverser {

  override def traverse(`do`: Do): Do = {
    val traversedBody = blockWrappingTermTraverser.traverse(`do`.body).block
    val traversedExpr = expressionTermTraverser.traverse(`do`.expr)
    Do(traversedBody, traversedExpr)
  }
}
