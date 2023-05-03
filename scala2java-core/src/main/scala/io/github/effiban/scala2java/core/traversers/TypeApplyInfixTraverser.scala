package io.github.effiban.scala2java.core.traversers

import scala.meta.Type

trait TypeApplyInfixTraverser extends ScalaTreeTraverser1[Type.ApplyInfix]

private[traversers] class TypeApplyInfixTraverserImpl extends TypeApplyInfixTraverser {

  // type with generic args in infix notation, e.g. K Map V
  override def traverse(typeApplyInfix: Type.ApplyInfix): Type.ApplyInfix = {
    //TODO - convert to Type.Apply
    typeApplyInfix
  }
}
