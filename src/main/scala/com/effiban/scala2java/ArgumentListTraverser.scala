package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter._

import scala.meta.Tree

object ArgumentListTraverser {

  def traverse[T <: Tree](args: List[T],
                          argTraverser: ScalaTreeTraverser[T],
                          onSameLine: Boolean = false,
                          maybeDelimiterType: Option[DualDelimiterType] = None): Unit = {
    maybeDelimiterType.foreach(emitArgumentsStart)
    args.zipWithIndex.foreach { case (tree, idx) =>
      argTraverser.traverse(tree)
      if (idx < args.size - 1) {
        emitListSeparator()
        if (args.size > 2 && !onSameLine) {
          emitLine()
        }
      }
    }
    maybeDelimiterType.foreach(emitArgumentsEnd)
  }
}
