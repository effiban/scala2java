package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{ArgumentContext, ArgumentListContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Tree

trait ArgumentListRenderer {
  def render[T <: Tree](args: List[T],
                        argRenderer: => ArgumentRenderer[T],
                        context: ArgumentListContext = ArgumentListContext()): Unit
}

class ArgumentListRendererImpl(implicit javaWriter: JavaWriter) extends ArgumentListRenderer {

  import javaWriter._

  override def render[T <: Tree](args: List[T],
                                 argRenderer: => ArgumentRenderer[T],
                                 context: ArgumentListContext = ArgumentListContext()): Unit = {
    if (args.nonEmpty || context.options.traverseEmpty) {

      import context.options._

      maybeEnclosingDelimiter.foreach(writeArgumentsStart)
      args.zipWithIndex.foreach { case (tree, idx) =>
        val argContext = ArgumentContext(
          maybeParent = context.maybeParent,
          index = idx,
          argNameAsComment = context.argNameAsComment
        )

        argRenderer.render(tree, argContext)
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
