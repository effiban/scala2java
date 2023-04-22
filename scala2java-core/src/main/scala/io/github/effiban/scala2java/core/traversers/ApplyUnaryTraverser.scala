package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer

import scala.meta.Term.ApplyUnary

trait ApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

private[traversers] class ApplyUnaryTraverserImpl(termNameRenderer: TermNameRenderer,
                                                  expressionTermTraverser: => TermTraverser) extends ApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    termNameRenderer.render(applyUnary.op)
    expressionTermTraverser.traverse(applyUnary.arg)
  }
}
