package effiban.scala2java.traversers

import effiban.scala2java.JavaEmitter
import effiban.scala2java.transformers.PatToTermParamTransformer

import scala.meta.Term
import scala.meta.Term.Block

trait TryTraverser extends ScalaTreeTraverser[Term.Try]

private[scala2java] class TryTraverserImpl(blockTraverser: => BlockTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser,
                                           patToTermParamTransformer: PatToTermParamTransformer)
                                          (implicit javaEmitter: JavaEmitter) extends TryTraverser {

  import javaEmitter._

  // TODO 1. support return value flag
  // TODO 2. Support case condition by moving into body
  override def traverse(`try`: Term.Try): Unit = {
    emit("try")
    `try`.expr match {
      case block: Block => blockTraverser.traverse(block)
      case stat => blockTraverser.traverse(Block(List(stat)))
    }
    `try`.catchp.foreach(`case` => {
      patToTermParamTransformer.transform(`case`.pat) match {
        case Some(param) => catchHandlerTraverser.traverse(param, `case`.body)
        case None => emitComment(s"UNPARSEABLE catch clause: ${`case`}")
      }
    })
    `try`.finallyp.foreach(finallyTraverser.traverse)
  }
}

object TryTraverser extends TryTraverserImpl(
  BlockTraverser,
  CatchHandlerTraverser,
  FinallyTraverser,
  PatToTermParamTransformer
)
