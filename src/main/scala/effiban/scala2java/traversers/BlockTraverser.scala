package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Block, If, Return, Throw, While}
import scala.meta.{Init, Stat, Term}

trait BlockTraverser {
  def traverse(block: Block, shouldReturnValue: Boolean = false, maybeInit: Option[Init] = None): Unit
}

private[traversers] class BlockTraverserImpl(initTraverser: => InitTraverser,
                                             ifTraverser: => IfTraverser,
                                             whileTraverser: => WhileTraverser,
                                             throwTraverser: => ThrowTraverser,
                                             returnTraverser: => ReturnTraverser,
                                             statTraverser: => StatTraverser)
                                            (implicit javaWriter: JavaWriter) extends BlockTraverser {

  import javaWriter._

  // The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
  // 'Init' does not inherit from 'Stat' so we can't add it to the Block
  override def traverse(block: Block, shouldReturnValue: Boolean = false, maybeInit: Option[Init] = None): Unit = {
    writeBlockStart()
    maybeInit.foreach(init => {
      initTraverser.traverse(init)
      writeStatementEnd()
    })
    traverseContents(block, shouldReturnValue)
    writeBlockEnd()
  }

  private def traverseContents(block: Block, shouldReturnValue: Boolean): Unit = {
    if (block.stats.nonEmpty) {
      block.stats.slice(0, block.stats.length - 1).foreach(traverseStatement(_))
      traverseStatement(block.stats.last, shouldReturnValue)
    }
  }

  private def traverseStatement(stat: Stat, shouldReturnValue: Boolean = false): Unit = {
    stat match {
      case block: Block => traverse(block, shouldReturnValue)
      case `if`: If => ifTraverser.traverse(`if`, shouldReturnValue)
      case `while`: While => whileTraverser.traverse(`while`)
      case `throw`: Throw =>
        throwTraverser.traverse(`throw`)
        writeStatementEnd()
      case term: Term if shouldReturnValue =>
        returnTraverser.traverse(Return(term))
        writeStatementEnd();
      case _ =>
        statTraverser.traverse(stat)
        writeStatementEnd()
    }
  }
}
