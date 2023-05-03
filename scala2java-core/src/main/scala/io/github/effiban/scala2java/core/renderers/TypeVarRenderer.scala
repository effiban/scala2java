package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Type

trait TypeVarRenderer extends JavaTreeRenderer[Type.Var]

private[renderers] class TypeVarRendererImpl(implicit javaWriter: JavaWriter) extends TypeVarRenderer {

  import javaWriter._

  // Variable in type, e.g.: `t` in `case _: List[t]` =>
  // Unsupported in Java and no replacement I can think of
  override def render(typeVar: Type.Var): Unit = {
    writeComment(typeVar.toString())
  }
}
