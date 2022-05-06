package com.effiban.scala2java

import scala.meta.Tree

trait ArgumentListTraverser {
  def traverse[T <: Tree](args: List[T],
                          argTraverser: ScalaTreeTraverser[T],
                          onSameLine: Boolean = false,
                          maybeDelimiterType: Option[DualDelimiterType] = None): Unit
}

class ArgumentListTraverserImpl(javaEmitter: JavaEmitter) extends ArgumentListTraverser {

  import javaEmitter._

  override def traverse[T <: Tree](args: List[T],
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

object ArgumentListTraverser extends ArgumentListTraverserImpl(JavaEmitter)
