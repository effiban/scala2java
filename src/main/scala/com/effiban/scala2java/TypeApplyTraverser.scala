package com.effiban.scala2java

import scala.meta.Type

trait TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply]

object TypeApplyTraverser extends TypeApplyTraverser {

  // type definition with generic args, e.g. F[T]
  override def traverse(typeApply: Type.Apply): Unit = {
    TypeTraverser.traverse(typeApply.tpe)
    TypeListTraverser.traverse(typeApply.args)
  }
}
