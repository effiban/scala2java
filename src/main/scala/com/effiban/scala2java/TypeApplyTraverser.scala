package com.effiban.scala2java

import scala.meta.Type

object TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply] {

  // type definition with generic args, e.g. F[T]
  def traverse(typeApply: Type.Apply): Unit = {
    TypeTraverser.traverse(typeApply.tpe)
    TypeListTraverser.traverse(typeApply.args)
  }
}
