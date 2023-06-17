package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser1[ApplyUnary]

private[traversers] class ApplyUnaryTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): ApplyUnary = {
    applyUnary.copy(arg = expressionTermTraverser.traverse(applyUnary.arg))
  }
}
