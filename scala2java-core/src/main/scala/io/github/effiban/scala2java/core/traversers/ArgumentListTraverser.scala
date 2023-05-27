package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, ArgumentListContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Tree

trait ArgumentListTraverser {
  def traverse[T <: Tree](args: List[T],
                          argTraverser: => ArgumentTraverser[T],
                          context: ArgumentListContext = ArgumentListContext()): Unit
}

class ArgumentListTraverserImpl(implicit javaWriter: JavaWriter) extends ArgumentListTraverser {

  import javaWriter._

  override def traverse[T <: Tree](args: List[T],
                                   argTraverser: => ArgumentTraverser[T],
                                   context: ArgumentListContext = ArgumentListContext()): Unit = {
    if (args.nonEmpty || context.options.traverseEmpty) {

      import context.options._

      maybeEnclosingDelimiter.foreach(writeArgumentsStart)
      args.zipWithIndex.foreach { case (tree, idx) =>
        val argContext = ArgumentContext(
          argNameAsComment = context.argNameAsComment
        )

        argTraverser.traverse(tree, argContext)
        if (idx < args.size - 1) {
          writeListSeparator()
          writeWhitespaceIfNeeded(args.size, onSameLine)
        }
      }
      maybeEnclosingDelimiter.foreach(writeArgumentsEnd)
    }
  }

  private def writeWhitespaceIfNeeded(numArgs: Int, onSameLine: Boolean): Unit = {
    (numArgs, onSameLine) match {
      case (1, _) =>
      case (2, _) => write(" ") // If only 2 args, multiline will not look good so ignoring the flag
      case (_, true) => write(" ")
      case (_, false) => writeLine()
    }
  }
}
