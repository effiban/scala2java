package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.traverseLastStatement
import com.effiban.scala2java.JavaEmitter.{emitBlockEnd, emitBlockStart, emitStatementEnd}

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
        GenericTreeTraverser.traverse(stat)
        stat match {
          case _: Block =>
          case _: If =>
          case _: While =>
          case _ => emitStatementEnd()
        }
      })
    traverseLastStatement(block.stats.last)
  }
}
