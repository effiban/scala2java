package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.AngleBracket

import scala.meta.Type

object TypeParamListTraverser {

  def traverse(typeParams: List[Type.Param]): Unit = {
    if (typeParams.nonEmpty) {
      ArgumentListTraverser.traverse(args = typeParams,
        argTraverser = TypeParamTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}
