package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emitBlockEnd, emitBlockStart, emitStatementEnd}

import scala.meta.Term.{Block, If, Return, While}
import scala.meta.{Init, Stat, Term}

trait BlockTraverser extends ScalaTreeTraverser[Block] {
  def traverse(block: Block, shouldReturnValue: Boolean, maybeInit: Option[Init] = None): Unit
}

private[scala2java] class BlockTraverserImpl(initTraverser: => InitTraverser,
                                             ifTraverser: => IfTraverser,
                                             statTraverser: => StatTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends BlockTraverser {

  override def traverse(block: Block): Unit = {
    traverse(block, shouldReturnValue = false, maybeInit = None)
  }

  // The 'init' param is passed by constructors, whose first statement must be a call to super or other ctor.
  // 'Init' does not inherit from 'Stat' so we can't add it to the Block
  override def traverse(block: Block, shouldReturnValue: Boolean, maybeInit: Option[Init] = None): Unit = {
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
      block.stats.slice(0, block.stats.length - 1)
        .foreach(stat => traverseStatement(stat))

      block.stats.last match {
        case lastIf: If => ifTraverser.traverseIf(`if` = lastIf, shouldReturnValue = shouldReturnValue)
        case lastTerm: Term if shouldReturnValue => traverseStatement(Return(lastTerm))
        case lastStat => traverseStatement(lastStat)
      }
    }
  }

  private def traverseStatement(stat: Stat): Unit = {
    statTraverser.traverse(stat)
    stat match {
      case _: Block =>
      case _: If =>
      case _: While =>
      case _ => emitStatementEnd()
    }
  }
}

object BlockTraverser extends BlockTraverserImpl(InitTraverser, IfTraverser, StatTraverser)
