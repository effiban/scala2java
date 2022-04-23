package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Lit
import scala.meta.Term.{Block, If}

object IfTraverser extends ScalaTreeTraverser[If] {

  override def traverse(`if`: If): Unit = {
    traverseIf(`if` = `if`, shouldReturnValue = false)
  }

  def traverseIf(`if`: If, shouldReturnValue: Boolean): Unit = {
    // TODO handle mods (what is this in an 'if'?...)
    emit("if (")
    TermTraverser.traverse(`if`.cond)
    emit(")")
    `if`.thenp match {
      case block: Block => BlockTraverser.traverse(block = block, shouldReturnValue = shouldReturnValue)
      case term => BlockTraverser.traverse(block = Block(List(term)), shouldReturnValue = shouldReturnValue)
    }
    `if`.elsep match {
      case block: Block =>
        emit("else")
        BlockTraverser.traverse(block = block, shouldReturnValue = shouldReturnValue)
      case Lit.Unit() =>
      case term =>
        emit("else")
        BlockTraverser.traverse(block = Block(List(term)), shouldReturnValue = shouldReturnValue)
    }
  }
}
