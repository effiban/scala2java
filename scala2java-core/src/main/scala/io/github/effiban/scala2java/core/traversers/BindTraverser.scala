package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.BindRenderer

import scala.meta.Pat
import scala.meta.Pat.Bind

trait BindTraverser extends ScalaTreeTraverser[Pat.Bind]

private[traversers] class BindTraverserImpl(bindRenderer: BindRenderer) extends BindTraverser {

  // Pattern match bind variable, e.g.: a @ A().
  override def traverse(patternBind: Bind): Unit = {
    //TODO - consider supporting in Java by converting to a guard?
    bindRenderer.render(patternBind)
  }
}
