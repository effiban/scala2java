package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat.Bind

trait BindTraverser extends ScalaTreeTraverser1[Bind]

object BindTraverser extends BindTraverser {

  // Pattern match bind variable, e.g.: a @ A().
  override def traverse(patternBind: Bind): Bind = {
    //TODO - consider supporting in Java by converting to a guard?
    patternBind
  }
}
