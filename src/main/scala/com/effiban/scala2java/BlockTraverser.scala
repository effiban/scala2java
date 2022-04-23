package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitBlockEnd, emitBlockStart, emitStatementEnd}
import com.effiban.scala2java.LastStatementTraverser.traverseLastStatement

import scala.meta.Term.{Block, If, While}

object BlockTraverser extends ScalaTreeTraverser[Block] {

  // block of code
  def traverse(block: Block): Unit = {
    emitBlockStart()
    traverseBlockContents(block)
    emitBlockEnd()
  }

  private def traverseBlockContents(block: Block): Unit = {
    block.stats.slice(0, block.stats.length - 1)
      .foreach(stat => {
        StatTraverser.traverse(stat)
        stat match {
          case _: Block =>
          case _: If =>
          case _: While =>
          case _ => emitStatementEnd()
        }
      })
    // TODO this is incorrect for `if` and `while`, they do not necessarily return
    traverseLastStatement(block.stats.last)
  }
}
