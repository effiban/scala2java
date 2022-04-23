package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term.{Block, If}

object IfTraverser extends ScalaTreeTraverser[If] {

  override def traverse(`if`: If): Unit = {
    // TODO handle mods (what is this in an 'if'?...)
    emit("if (")
    TermTraverser.traverse(`if`.cond)
    emit(")")
    `if`.thenp match {
      case block: Block => BlockTraverser.traverse(block)
      case stmt =>
        emitBlockStart()
        TermTraverser.traverse(stmt)
        emitBlockEnd()
    }
    // TODO handle empty else (how is this done??)
    `if`.elsep match {
      case block: Block =>
        emit("else")
        BlockTraverser.traverse(block)
      case stmt =>
        emit("else")
        emitBlockStart()
        TermTraverser.traverse(stmt)
        emitBlockEnd()
    }
  }
}
