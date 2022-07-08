package effiban.scala2java

import scala.meta.Term
import scala.meta.Term.Block

trait CatchHandlerTraverser {
  def traverse(param: Term.Param, body: Term): Unit
}

private[scala2java] class CatchHandlerTraverserImpl(termParamListTraverser: => TermParamListTraverser,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaEmitter: JavaEmitter) extends CatchHandlerTraverser {

  import javaEmitter._

  // TODO - support return value flag
  override def traverse(param: Term.Param, body: Term): Unit = {
    emit("catch ")
    termParamListTraverser.traverse(termParams = List(param), onSameLine = true)
    body match {
      case block: Block => blockTraverser.traverse(block)
      case term: Term => blockTraverser.traverse(Block(List(term)))
    }
  }
}

object CatchHandlerTraverser extends CatchHandlerTraverserImpl(TermParamListTraverser, BlockTraverser)