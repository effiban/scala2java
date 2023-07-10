package io.github.effiban.scala2java.core.desugarers.syntactic

import io.github.effiban.scala2java.core.desugarers.DifferentTypeDesugarer

import scala.meta.Decl
import scala.meta.Mod.Final

trait DeclValToDeclVarDesugarer extends DifferentTypeDesugarer[Decl.Val, Decl.Var]

object DeclValToDeclVarDesugarer extends DeclValToDeclVarDesugarer {

  override def desugar(declVal: Decl.Val): Decl.Var = {
    Decl.Var(
      mods = declVal.mods :+ Final(),
      pats = declVal.pats,
      decltpe = declVal.decltpe
    )
  }
}

