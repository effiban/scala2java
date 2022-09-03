package effiban.scala2java.traversers

import effiban.scala2java.entities.Decision.{Decision, No}
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.Block
import scala.meta.{Init, Stat}

trait BlockTraverser {

  // The input is a Stat and not a Block, because sometimes we want to wrap a single Scala statement in a Java block
  // (which is convenient for both the translation logic and the formatting)
  // TODO - when expecting a return value which is a lambda, need another flag for returnability inside the lambda body
  def traverse(stat: Stat, shouldReturnValue: Decision = No, maybeInit: Option[Init] = None): Unit
}

private[traversers] class BlockTraverserImpl(initTraverser: => InitTraverser,
                                             blockStatTraverser: => BlockStatTraverser)
                                            (implicit javaWriter: JavaWriter) extends BlockTraverser {

  import javaWriter._

  // The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
  // 'Init' does not inherit from 'Stat' so we can't add it to the Block
  override def traverse(stat: Stat, shouldReturnValue: Decision = No, maybeInit: Option[Init] = None): Unit = {
    val block = stat match {
      case blk: Block => blk
      case st => Block(List(st))
    }
    traverseBlock(block, shouldReturnValue, maybeInit)
  }

  private def traverseBlock(block: Block, shouldReturnValue: Decision, maybeInit: Option[Init] = None): Unit = {
    writeBlockStart()
    maybeInit.foreach(init => {
      initTraverser.traverse(init)
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
