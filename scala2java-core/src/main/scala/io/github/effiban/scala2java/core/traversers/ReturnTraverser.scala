package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.Return

trait ReturnTraverser extends ScalaTreeTraverser1[Return]

private[traversers] class ReturnTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends ReturnTraverser {

  override def traverse(`return`: Return): Return = {
    `return`.copy(expr = expressionTermTraverser.traverse(`return`.expr))
  }
}
