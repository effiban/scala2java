package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitBlockEnd, emitBlockStart, emitStatementEnd}

import scala.meta.Term.{Block, If, Return, While}
import scala.meta.{Stat, Term}

object BlockTraverser extends ScalaTreeTraverser[Block] {

  override def traverse(block: Block): Unit = {
    traverse(block, shouldReturnValue = false)
  }

  def traverse(block: Block, shouldReturnValue: Boolean): Unit = {
    emitBlockStart()
    traverseContents(block, shouldReturnValue)
    emitBlockEnd()
  }

  private def traverseContents(block: Block, shouldReturnValue: Boolean): Unit = {
    block.stats.slice(0, block.stats.length - 1)
      .foreach(stat => traverseStatement(stat))

    block.stats.last match {
      case lastIf: If => IfTraverser.traverseIf(`if` = lastIf, shouldReturnValue = shouldReturnValue)
      case lastTerm: Term if shouldReturnValue => traverseStatement(Return(lastTerm))
      case lastStat => traverseStatement(lastStat)
    }
  }

  private def traverseStatement(stat: Stat): Unit = {
    StatTraverser.traverse(stat)
    stat match {
      case _: Block =>
      case _: If =>
      case _: While =>
      case _ => emitStatementEnd()
    }
  }
}
