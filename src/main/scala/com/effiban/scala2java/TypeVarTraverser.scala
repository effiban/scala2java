package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Type

trait TypeVarTraverser extends ScalaTreeTraverser[Type.Var]

private[scala2java] class TypeVarTraverserImpl(javaEmitter: JavaEmitter) extends TypeVarTraverser {

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    emitComment(typeVar.toString())
  }
}

object TypeVarTraverser extends TypeVarTraverserImpl(JavaEmitter)
