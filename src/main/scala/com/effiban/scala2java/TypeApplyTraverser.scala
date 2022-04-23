package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.traverseGenericTypeList

import scala.meta.Type

object TypeApplyTraverser extends ScalaTreeTraverser[Type.Apply] {

  // type definition with generic args, e.g. F[T]
  def traverse(typeApply: Type.Apply): Unit = {
    GenericTreeTraverser.traverse(typeApply.tpe)
    traverseGenericTypeList(typeApply.args)
  }
}
