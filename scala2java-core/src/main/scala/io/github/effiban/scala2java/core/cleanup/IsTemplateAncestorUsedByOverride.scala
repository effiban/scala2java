package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.reflection.ScalaReflectionUtils.isTermMemberOf

import scala.meta.{Decl, Defn, Pat, Template, Type}

object IsTemplateAncestorUsedByOverride extends IsTemplateAncestorUsed {

  def apply(template: Template, ancestorType: Type.Ref): Boolean =
    template.stats.collectFirst {
      // Since the 'override' modifier is not required by the compiler,
      // we need to check all data members and methods
      case declVar@Decl.Var(_, List(patVar: Pat.Var), _) if isTermMemberOf(ancestorType, patVar.name) => declVar
      case defnVar@Defn.Var(_, List(patVar: Pat.Var), _, _) if isTermMemberOf(ancestorType, patVar.name) => defnVar
      // TODO - compare method signatures exactly
      case declDef : Decl.Def if isTermMemberOf(ancestorType, declDef.name) => declDef
      case defnDef : Defn.Def if isTermMemberOf(ancestorType, defnDef.name) => defnDef
    }.nonEmpty
}