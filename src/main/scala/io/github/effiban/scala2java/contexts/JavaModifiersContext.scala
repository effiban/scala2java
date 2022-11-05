package io.github.effiban.scala2java.contexts

import io.github.effiban.scala2java.entities.JavaScope.JavaScope
import io.github.effiban.scala2java.entities.JavaTreeType.JavaTreeType

import scala.meta.Stat.WithMods
import scala.meta.{Mod, Term, Tree}

case class JavaModifiersContext(scalaTree: Tree,
                                javaTreeType: JavaTreeType,
                                javaScope: JavaScope) {

  val scalaMods: List[Mod] = scalaTree match {
    case statWithMods: WithMods => statWithMods.mods
    case termParam : Term.Param => termParam.mods
    case _ => Nil
  }
}
