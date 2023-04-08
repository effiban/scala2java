package io.github.effiban.scala2java.core.traversers

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

private[traversers] class ApplyUnaryTraverserImpl(termNameTraverser: => TermNameTraverser,
                                                  expressionTermTraverser: => TermTraverser) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    termNameTraverser.traverse(applyUnary.op)
    expressionTermTraverser.traverse(applyUnary.arg)
  }
}
