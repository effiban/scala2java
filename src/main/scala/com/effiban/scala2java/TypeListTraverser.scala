package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.AngleBracket

import scala.meta.Tree

object TypeListTraverser {

  def traverse(types: List[Tree]): Unit = {
    if (types.nonEmpty) {
      ArgumentListTraverser.traverse(list = types, maybeDelimiterType = Some(AngleBracket), onSameLine = true)
    }
  }
}
