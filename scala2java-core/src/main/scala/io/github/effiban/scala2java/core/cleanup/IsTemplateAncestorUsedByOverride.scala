package io.github.effiban.scala2java.core.cleanup

import io.github.effiban.scala2java.core.reflection.ScalaReflectionLookup

import scala.meta.{Decl, Defn, Pat, Template, Type}

private[cleanup] class IsTemplateAncestorUsedByOverrideImpl(scalaReflectionLookup: ScalaReflectionLookup)
  extends IsTemplateAncestorUsed {

  override def apply(template: Template, ancestorType: Type.Ref): Boolean = {
    import scalaReflectionLookup._

    template.stats.collectFirst {
      // Since the 'override' modifier is not required by the compiler,
      // we need to check all data members and methods
      // TODO - perform the entire logic by refection if possible
      case declVar@Decl.Var(_, List(patVar: Pat.Var), _) if isTermMemberOf(ancestorType, patVar.name) => declVar
      case defnVar@Defn.Var(_, List(patVar: Pat.Var), _, _) if isTermMemberOf(ancestorType, patVar.name) => defnVar
      // TODO - compare method signatures exactly
      case declDef: Decl.Def if isTermMemberOf(ancestorType, declDef.name) => declDef
      case defnDef: Defn.Def if isTermMemberOf(ancestorType, defnDef.name) => defnDef
    }.nonEmpty
  }
}

object IsTemplateAncestorUsedByOverride extends IsTemplateAncestorUsedByOverrideImpl(ScalaReflectionLookup)