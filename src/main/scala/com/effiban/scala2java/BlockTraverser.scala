package com.effiban.scala2java

import scala.meta.Term.{Block, If, Return, While}
import scala.meta.{Init, Stat, Term}

trait BlockTraverser {
  def traverse(block: Block, shouldReturnValue: Boolean = false, maybeInit: Option[Init] = None): Unit
}

private[scala2java] class BlockTraverserImpl(initTraverser: => InitTraverser,
                                             ifTraverser: => IfTraverser,
                                             whileTraverser: WhileTraverser,
                                             returnTraverser: ReturnTraverser,
                                             statTraverser: => StatTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends BlockTraverser {

  import javaEmitter._

  // The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
  // 'Init' does not inherit from 'Stat' so we can't add it to the Block
  override def traverse(block: Block, shouldReturnValue: Boolean = false, maybeInit: Option[Init] = None): Unit = {
    emitBlockStart()
    maybeInit.foreach(init => {
      initTraverser.traverse(init)
      emitStatementEnd()
    })
    traverseContents(block, shouldReturnValue)
    emitBlockEnd()
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
      case term: Term if shouldReturnValue =>
        returnTraverser.traverse(Return(term))
        emitStatementEnd();
      case _ =>
        statTraverser.traverse(stat)
        emitStatementEnd()
    }
  }
}

object BlockTraverser extends BlockTraverserImpl(
  InitTraverser,
  IfTraverser,
  WhileTraverser,
  ReturnTraverser,
  StatTraverser
)
