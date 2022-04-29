package com.effiban.scala2java

import scala.meta.Pat

trait PatVarTraverser extends ScalaTreeTraverser[Pat.Var]

object PatVarTraverser extends PatVarTraverser {

  // Pattern match variable, e.g. `a` in case a =>
  override def traverse(patternVar: Pat.Var): Unit = {
    TermNameTraverser.traverse(patternVar.name)
  }
}
