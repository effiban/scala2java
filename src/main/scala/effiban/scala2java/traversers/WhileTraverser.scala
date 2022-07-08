package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Block, While}

trait WhileTraverser extends ScalaTreeTraverser[While]

private[scala2java] class WhileTraverserImpl(termTraverser: => TermTraverser,
                                             blockTraverser: BlockTraverser)
                                            (implicit javaWriter: JavaWriter) extends WhileTraverser {

  import javaWriter._

  override def traverse(`while`: While): Unit = {
    write("while (")
    termTraverser.traverse(`while`.expr)
    write(")")
    `while`.body match {
      case block: Block => blockTraverser.traverse(block)
      case term => blockTraverser.traverse(block = Block(List(term)))
    }
  }
}

object WhileTraverser extends WhileTraverserImpl(TermTraverser, BlockTraverser)