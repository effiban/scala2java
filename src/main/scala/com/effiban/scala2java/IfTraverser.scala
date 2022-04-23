package com.effiban.scala2java

import com.effiban.scala2java.GenericTreeTraverser.traverseLastStatement
import com.effiban.scala2java.JavaEmitter.{emit, emitBlockEnd, emitBlockStart}

import scala.meta.Term.{Block, If}

object IfTraverser extends ScalaTreeTraverser[If] {

  override def traverse(`if`: If): Unit = {
    // TODO handle mods (what is this in an 'if'?...)
    emit("if (")
    GenericTreeTraverser.traverse(`if`.cond)
    emit(")")
    `if`.thenp match {
      case block: Block => GenericTreeTraverser.traverse(block)
      case stmt =>
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
    }
    `if`.elsep match {
      case block: Block =>
        emit("else")
        GenericTreeTraverser.traverse(block)
      case stmt =>
        emit("else")
        emitBlockStart()
        traverseLastStatement(stmt)
        emitBlockEnd()
    }
  }
}
