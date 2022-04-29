package com.effiban.scala2java

import scala.meta.Type

trait TypeParamListTraverser {
  def traverse(typeParams: List[Type.Param]): Unit
}

object TypeParamListTraverser extends TypeParamListTraverser {

  override def traverse(typeParams: List[Type.Param]): Unit = {
    if (typeParams.nonEmpty) {
      ArgumentListTraverser.traverse(args = typeParams,
        argTraverser = TypeParamTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}
