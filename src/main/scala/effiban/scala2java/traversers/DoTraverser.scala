package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Block, Do}

trait DoTraverser extends ScalaTreeTraverser[Do]

private[traversers] class DoTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: BlockTraverser)
                                         (implicit javaWriter: JavaWriter) extends DoTraverser {

  import javaWriter._

  override def traverse(`do`: Do): Unit = {
    write("do")
    `do`.body match {
      case block: Block => blockTraverser.traverse(block)
      case term => blockTraverser.traverse(block = Block(List(term)))
    }
    write(" while (")
    termTraverser.traverse(`do`.expr)
    write(")")
  }
}
