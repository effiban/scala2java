package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Defn
import scala.meta.Defn.Trait

object DefnTraverser extends ScalaTreeTraverser[Defn] {

  override def traverse(defn: Defn): Unit = defn match {
    case valDef: Defn.Val => DefnValTraverser.traverse(valDef)
    case varDef: Defn.Var => DefnVarTraverser.traverse(varDef)
    case defDef: Defn.Def => DefnDefTraverser.traverse(defDef)
    case typeDef: Defn.Type => DefnTypeTraverser.traverse(typeDef)
    case classDef: Defn.Class => ClassTraverser.traverse(classDef)
    case traitDef: Trait => TraitTraverser.traverse(traitDef)
    case objectDef: Defn.Object => ObjectTraverser.traverse(objectDef)
    case _ => emitComment(s"UNSUPPORTED: $defn")
  }
}
