package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

object TypeVarTraverser extends ScalaTreeTraverser[Type.Var] {

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    emitComment(typeVar.toString())
  }
}
