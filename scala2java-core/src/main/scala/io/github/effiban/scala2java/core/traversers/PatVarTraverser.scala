package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TermNameRenderer

import scala.meta.Pat

trait PatVarTraverser extends ScalaTreeTraverser[Pat.Var]

private[traversers] class PatVarTraverserImpl(termNameRenderer: TermNameRenderer) extends PatVarTraverser {

  // Pattern match variable, e.g. `a` in case a =>
  override def traverse(patternVar: Pat.Var): Unit = {
    termNameRenderer.render(patternVar.name)
  }
}
