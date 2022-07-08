package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.EnclosingDelimiter
import effiban.scala2java.writers.JavaWriter

import scala.meta.Tree

trait ArgumentListTraverser {
  def traverse[T <: Tree](args: List[T],
                          argTraverser: ScalaTreeTraverser[T],
                          onSameLine: Boolean = false,
                          maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None): Unit
}

class ArgumentListTraverserImpl(implicit javaWriter: JavaWriter) extends ArgumentListTraverser {

  import javaWriter._

  override def traverse[T <: Tree](args: List[T],
                                   argTraverser: ScalaTreeTraverser[T],
                                   onSameLine: Boolean = false,
                                   maybeEnclosingDelimiter: Option[EnclosingDelimiter] = None): Unit = {
    maybeEnclosingDelimiter.foreach(writeArgumentsStart)
    args.zipWithIndex.foreach { case (tree, idx) =>
      argTraverser.traverse(tree)
      if (idx < args.size - 1) {
        writeListSeparator()
        writeWhitespaceIfNeeded(args.size, onSameLine)
      }
    }
    maybeEnclosingDelimiter.foreach(writeArgumentsEnd)
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

object ArgumentListTraverser extends ArgumentListTraverserImpl
