package com.effiban.scala2java

import scala.meta.Lit
import scala.meta.Term.{Block, If}

trait IfTraverser {
  def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit
}

private[scala2java] class IfTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: => BlockTraverser)
                                         (implicit javaEmitter: JavaEmitter) extends IfTraverser {

  import javaEmitter._

  override def traverse(`if`: If, shouldReturnValue: Boolean = false): Unit = {
    // TODO handle mods (what is this in an 'if'?...)
    emit("if (")
    termTraverser.traverse(`if`.cond)
    emit(")")
    `if`.thenp match {
      case block: Block => blockTraverser.traverse(block = block, shouldReturnValue = shouldReturnValue)
      case term => blockTraverser.traverse(block = Block(List(term)), shouldReturnValue = shouldReturnValue)
    }
    `if`.elsep match {
      case block: Block =>
        emit("else")
        blockTraverser.traverse(block = block, shouldReturnValue = shouldReturnValue)
      case Lit.Unit() =>
      case term =>
        emit("else")
        blockTraverser.traverse(block = Block(List(term)), shouldReturnValue = shouldReturnValue)
    }
  }
}

object IfTraverser extends IfTraverserImpl(TermTraverser, BlockTraverser)
