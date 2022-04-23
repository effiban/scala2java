package com.effiban.scala2java

import scala.meta.Pat

object PatVarTraverser extends ScalaTreeTraverser[Pat.Var] {

  // Pattern match variable, e.g. `a` in case a =>
  def traverse(patternVar: Pat.Var): Unit = {
    TermNameTraverser.traverse(patternVar.name)
  }
}
