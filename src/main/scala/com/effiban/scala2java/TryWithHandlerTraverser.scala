package com.effiban.scala2java

import scala.meta.Term
import scala.meta.Term.{Block, TryWithHandler}

trait TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler]

private[scala2java] class TryWithHandlerTraverserImpl(blockTraverser: => BlockTraverser,
                                                      catchHandlerTraverser: => CatchHandlerTraverser,
                                                      finallyTraverser: => FinallyTraverser)
                                                     (implicit javaEmitter: JavaEmitter) extends TryWithHandlerTraverser {
  import javaEmitter._

  // TODO support return value flag
  override def traverse(tryWithHandler: TryWithHandler): Unit = {
    emit("try")
    tryWithHandler.expr match {
      case block: Block => blockTraverser.traverse(block)
      case stat => blockTraverser.traverse(Block(List(stat)))
    }
    tryWithHandler.catchp match {
      case Term.Function(List(param), body) => catchHandlerTraverser.traverse(param, body)
      case _ =>
        emitComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
        emitLine()
    }
    tryWithHandler.finallyp.foreach(finallyTraverser.traverse)
  }
}

object TryWithHandlerTraverser extends TryWithHandlerTraverserImpl(
  BlockTraverser,
  CatchHandlerTraverser,
  FinallyTraverser
)
