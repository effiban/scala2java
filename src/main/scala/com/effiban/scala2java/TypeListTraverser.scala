package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.AngleBracket

import scala.meta.Type

object TypeListTraverser {

  def traverse(types: List[Type]): Unit = {
    if (types.nonEmpty) {
      ArgumentListTraverser.traverse(args = types,
        argTraverser = TypeTraverser,
        maybeDelimiterType = Some(AngleBracket),
        onSameLine = true)
    }
  }
}
