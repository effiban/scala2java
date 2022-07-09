package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait CatchHandlerTraverser {
  def traverse(param: Term.Param, body: Term): Unit
}

private[traversers] class CatchHandlerTraverserImpl(termParamListTraverser: => TermParamListTraverser,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends CatchHandlerTraverser {

  import javaWriter._

  // TODO - support return value flag
  override def traverse(param: Term.Param, body: Term): Unit = {
    write("catch ")
    termParamListTraverser.traverse(termParams = List(param), onSameLine = true)
    body match {
      case block: Block => blockTraverser.traverse(block)
      case term: Term => blockTraverser.traverse(Block(List(term)))
    }
  }
}
