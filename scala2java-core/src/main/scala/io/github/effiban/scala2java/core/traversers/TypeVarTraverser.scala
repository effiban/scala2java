package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeVarTraverser extends ScalaTreeTraverser[Type.Var]

private[traversers] class TypeVarTraverserImpl(implicit javaWriter: JavaWriter) extends TypeVarTraverser {

  import javaWriter._

  // Variable in type, e.g.: `t` in case _:List(t) =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    writeComment(typeVar.toString())
  }
}