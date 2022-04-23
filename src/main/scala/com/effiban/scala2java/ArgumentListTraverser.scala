package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._

import scala.meta.Tree

object ArgumentListTraverser {

  def traverse(list: List[_ <: Tree],
               onSameLine: Boolean = false,
               maybeDelimiterType: Option[DualDelimiterType] = None): Unit = {
    maybeDelimiterType.foreach(emitArgumentsStart)
    list.zipWithIndex.foreach { case (tree, idx) =>
      GenericTreeTraverser.traverse(tree)
      if (idx < list.size - 1) {
        emitListSeparator()
        if (list.size > 2 && !onSameLine) {
          emitLine()
        }
      }
    }
    maybeDelimiterType.foreach(emitArgumentsEnd)
  }
}
