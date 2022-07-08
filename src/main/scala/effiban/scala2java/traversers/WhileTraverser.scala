package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter

import scala.meta.Term.{Block, While}

trait WhileTraverser extends ScalaTreeTraverser[While]

private[scala2java] class WhileTraverserImpl(termTraverser: => TermTraverser,
                                             blockTraverser: BlockTraverser)
                                            (implicit javaEmitter: JavaEmitter) extends WhileTraverser {

  import javaEmitter._

  override def traverse(`while`: While): Unit = {
    emit("while (")
    termTraverser.traverse(`while`.expr)
    emit(")")
    `while`.body match {
      case block: Block => blockTraverser.traverse(block)
      case term => blockTraverser.traverse(block = Block(List(term)))
    }
  }
}

object WhileTraverser extends WhileTraverserImpl(TermTraverser, BlockTraverser)