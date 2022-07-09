package effiban.scala2java.traversers

import effiban.scala2java.transformers.PatToTermParamTransformer
import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait TryTraverser extends ScalaTreeTraverser[Term.Try]

private[traversers] class TryTraverserImpl(blockTraverser: => BlockTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser,
                                           patToTermParamTransformer: PatToTermParamTransformer)
                                          (implicit javaWriter: JavaWriter) extends TryTraverser {

  import javaWriter._

  // TODO 1. support return value flag
  // TODO 2. Support case condition by moving into body
  override def traverse(`try`: Term.Try): Unit = {
    write("try")
    `try`.expr match {
      case block: Block => blockTraverser.traverse(block)
      case stat => blockTraverser.traverse(Block(List(stat)))
    }
    `try`.catchp.foreach(`case` => {
      patToTermParamTransformer.transform(`case`.pat) match {
        case Some(param) => catchHandlerTraverser.traverse(param, `case`.body)
        case None => writeComment(s"UNPARSEABLE catch clause: ${`case`}")
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
