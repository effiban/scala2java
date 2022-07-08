package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term
import scala.meta.Term.Block

trait FinallyTraverser extends ScalaTreeTraverser[Term]

private[scala2java] class FinallyTraverserImpl(blockTraverser: => BlockTraverser)
                                              (implicit javaEmitter: JavaEmitter) extends FinallyTraverser {

  import javaEmitter._

  // TODO support return value flag
  override def traverse(finallyp: Term): Unit = {
    emit("finally")
    finallyp match {
      case block: Block => blockTraverser.traverse(block)
      case term => blockTraverser.traverse(Block(List(term)))
    }
  }
}

object FinallyTraverser extends FinallyTraverserImpl(BlockTraverser)
