package com.effiban.scala2java

import scala.meta.Type

trait TypeListTraverser {
  def traverse(types: List[Type]): Unit
}

object TypeListTraverser extends TypeListTraverser {

  override def traverse(types: List[Type]): Unit = {
    if (types.nonEmpty) {
      ArgumentListTraverser.traverse(args = types,
        argTraverser = TypeTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}
