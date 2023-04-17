package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeVarRenderer

import scala.meta.Type

trait TypeVarTraverser extends ScalaTreeTraverser[Type.Var]

private[traversers] class TypeVarTraverserImpl(typeVarRenderer: TypeVarRenderer) extends TypeVarTraverser {

  // Variable in type, e.g.: `t` in `case _: List[t]` =>
  // Unsupported in Java and no replacement I can think of
  override def traverse(typeVar: Type.Var): Unit = {
    typeVarRenderer.render(typeVar)
  }
}
