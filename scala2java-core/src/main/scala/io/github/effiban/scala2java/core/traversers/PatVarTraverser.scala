package io.github.effiban.scala2java.core.traversers

import scala.meta.Pat

trait PatVarTraverser extends ScalaTreeTraverser[Pat.Var]

private[traversers] class PatVarTraverserImpl(defaultTermNameTraverser: => TermNameTraverser) extends PatVarTraverser {

  // Pattern match variable, e.g. `a` in case a =>
  override def traverse(patternVar: Pat.Var): Unit = {
    defaultTermNameTraverser.traverse(patternVar.name)
  }
}
