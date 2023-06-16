package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer

import scala.meta.Term.ApplyUnary

@deprecated
trait DeprecatedApplyUnaryTraverser extends ScalaTreeTraverser[ApplyUnary]

@deprecated
private[traversers] class DeprecatedApplyUnaryTraverserImpl(termNameRenderer: TermNameRenderer,
                                                            expressionTermTraverser: => DeprecatedExpressionTermTraverser) extends DeprecatedApplyUnaryTraverser {

  override def traverse(applyUnary: ApplyUnary): Unit = {
    termNameRenderer.render(applyUnary.op)
    expressionTermTraverser.traverse(applyUnary.arg)
  }
}
