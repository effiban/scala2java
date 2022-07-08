package effiban.scala2java

import scala.meta.Term.{Block, Do}

trait DoTraverser extends ScalaTreeTraverser[Do]

private[scala2java] class DoTraverserImpl(termTraverser: => TermTraverser,
                                          blockTraverser: BlockTraverser)
                                         (implicit javaEmitter: JavaEmitter) extends DoTraverser {

  import javaEmitter._

  override def traverse(`do`: Do): Unit = {
    emit("do")
    `do`.body match {
      case block: Block => blockTraverser.traverse(block)
      case term => blockTraverser.traverse(block = Block(List(term)))
    }
    emit(" while (")
    termTraverser.traverse(`do`.expr)
    emit(")")
  }
}

object DoTraverser extends DoTraverserImpl(TermTraverser, BlockTraverser)
