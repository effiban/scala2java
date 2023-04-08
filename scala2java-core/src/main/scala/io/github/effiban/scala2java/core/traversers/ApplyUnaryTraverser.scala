package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

private[traversers] class ApplyUnaryTraverserImpl(defaultTermNameTraverser: => TermNameTraverser,
                                                  expressionTermTraverser: => TermTraverser) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    defaultTermNameTraverser.traverse(applyUnary.op)
    expressionTermTraverser.traverse(applyUnary.arg)
  }
}
