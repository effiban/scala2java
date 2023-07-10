package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.Defn
import scala.meta.Mod.Final

trait DefnValToDefnVarDesugarer extends DifferentTypeDesugarer[Defn.Val, Defn.Var]

object DefnValToDefnVarDesugarer extends DefnValToDefnVarDesugarer {

  override def desugar(defnVal: Defn.Val): Defn.Var = {
    Defn.Var(
      mods = defnVal.mods :+ Final(),
      pats = defnVal.pats,
      decltpe = defnVal.decltpe,
      rhs = Some(defnVal.rhs)
    )
  }
}

