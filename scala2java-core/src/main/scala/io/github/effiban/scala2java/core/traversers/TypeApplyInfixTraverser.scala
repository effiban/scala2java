package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.renderers.TypeApplyInfixRenderer

import scala.meta.Type

trait TypeApplyInfixTraverser extends ScalaTreeTraverser[Type.ApplyInfix]

private[traversers] class TypeApplyInfixTraverserImpl(typeApplyInfixRenderer: TypeApplyInfixRenderer) extends TypeApplyInfixTraverser {

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Unit = {
    //TODO - convert to Type.Apply instead of rendering
    typeApplyInfixRenderer.render(typeApplyInfix)
  }
}
