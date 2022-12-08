package io.github.effiban.scala2java.core.contexts

import io.github.effiban.scala2java.core.entities.JavaTreeType.JavaTreeType
import io.github.effiban.scala2java.spi.entities.JavaScope.JavaScope

import scala.meta.Stat.WithMods
import scala.meta.{Mod, Term, Tree}

case class ModifiersContext(scalaTree: Tree,
                            javaTreeType: JavaTreeType,
                            javaScope: JavaScope) {

  val scalaMods: List[Mod] = scalaTree match {
    case statWithMods: WithMods => statWithMods.mods
    case termParam : Term.Param => termParam.mods
    case _ => Nil
  }
}
