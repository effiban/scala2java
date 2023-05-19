package io.github.effiban.scala2java.core.desugarers

import scala.meta.Defn
import scala.meta.Defn.Trait

trait DefnDesugarer extends SameTypeDesugarer[Defn]

private[desugarers] class DefnDesugarerImpl() extends DefnDesugarer {

  override def desugar(defn: Defn): Defn = defn match {
    case valDef: Defn.Val => valDef // TODO
    case varDef: Defn.Var => varDef // TODO
    case defDef: Defn.Def => defDef // TODO
    case typeDef: Defn.Type => typeDef // TODO
    case classDef: Defn.Class => classDef // TODO
    case traitDef: Trait => traitDef // TODO
    case objectDef: Defn.Object => objectDef // TODO
    case other => other
  }

}
