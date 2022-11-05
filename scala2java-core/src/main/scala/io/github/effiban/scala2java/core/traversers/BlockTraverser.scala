package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, InitContext}
import io.github.effiban.scala2java.core.entities.Decision.Decision
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Stat
import scala.meta.Term.Block

trait BlockTraverser {

  // The input is a Stat and not a Block, because sometimes we want to wrap a single Scala statement in a Java block
  // (which is convenient for both the translation logic and the formatting)
  def traverse(stat: Stat, context: BlockContext = BlockContext()): Unit
}

private[traversers] class BlockTraverserImpl(initTraverser: => InitTraverser,
                                             blockStatTraverser: => BlockStatTraverser)
                                            (implicit javaWriter: JavaWriter) extends BlockTraverser {

  import javaWriter._

  override def traverse(stat: Stat, context: BlockContext = BlockContext()): Unit = {
    val block = stat match {
      case blk: Block => blk
      case st => Block(List(st))
    }
    traverseBlock(block, context)
  }

  private def traverseBlock(block: Block, context: BlockContext): Unit = {
    import context._

    writeBlockStart()
    maybeInit.foreach(init => {
      initTraverser.traverse(init, InitContext(argNameAsComment = true))
      writeStatementEnd()
    })
    traverseContents(block, shouldReturnValue)
    writeBlockEnd()
  }

  private def traverseContents(block: Block, shouldReturnValue: Decision): Unit = {
    if (block.stats.nonEmpty) {
      block.stats.slice(0, block.stats.length - 1).foreach(blockStatTraverser.traverse)
      blockStatTraverser.traverseLast(block.stats.last, shouldReturnValue)
    }
  }
}
