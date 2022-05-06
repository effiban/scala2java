package com.effiban.scala2java

import scala.meta.Tree

trait ArgumentListTraverser {
  def traverse[T <: Tree](args: List[T],
                          argTraverser: ScalaTreeTraverser[T],
                          onSameLine: Boolean = false,
                          maybeDelimiterType: Option[DualDelimiterType] = None): Unit
}

class ArgumentListTraverserImpl(implicit javaEmitter: JavaEmitter) extends ArgumentListTraverser {

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
        emitWhitespaceIfNeeded(args.size, onSameLine)
      }
    }
    maybeWrappingDelimiterType.foreach(emitArgumentsEnd)
  }

  private def emitWhitespaceIfNeeded(numArgs: Int, onSameLine: Boolean): Unit = {
    (numArgs, onSameLine) match {
      case (1, _) =>
      case (2, _) => emit(" ") // If only 2 args, multiline will not look good so ignoring the flag
      case (_, true) => emit(" ")
      case (_, false) => emitLine()
    }
  }
}

object ArgumentListTraverser extends ArgumentListTraverserImpl
