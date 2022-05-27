package com.effiban.scala2java.stubs

import com.effiban.scala2java.{ArgumentListTraverser, DualDelimiterType, JavaEmitter, ScalaTreeTraverser}

import scala.meta.Tree

class StubArgumentListTraverser(implicit javaEmitter: JavaEmitter) extends ArgumentListTraverser {

  import javaEmitter._

  override def traverse[T <: Tree](args: List[T],
                                   argTraverser: ScalaTreeTraverser[T],
                                   onSameLine: Boolean = false,
                                   maybeWrappingDelimiterType: Option[DualDelimiterType] = None): Unit = {
    maybeWrappingDelimiterType.foreach(emitArgumentsStart)
    args.zipWithIndex.foreach { case (tree, idx) =>
      argTraverser.traverse(tree)
      if (idx < args.size - 1) {
        emitListSeparator()
        if (onSameLine) emit(" ") else emitLine()
      }
    }
    maybeWrappingDelimiterType.foreach(emitArgumentsEnd)
  }
}
