package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Block, TryWithHandler}

trait TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler]

private[traversers] class TryWithHandlerTraverserImpl(blockTraverser: => BlockTraverser,
                                                      catchHandlerTraverser: => CatchHandlerTraverser,
                                                      finallyTraverser: => FinallyTraverser)
                                                     (implicit javaWriter: JavaWriter) extends TryWithHandlerTraverser {

  import javaWriter._

  // TODO support return value flag
  override def traverse(tryWithHandler: TryWithHandler): Unit = {
    write("try")
    tryWithHandler.expr match {
      case block: Block => blockTraverser.traverse(block)
      case stat => blockTraverser.traverse(Block(List(stat)))
    }
    tryWithHandler.catchp match {
      case Term.Function(List(param), body) => catchHandlerTraverser.traverse(param, body)
      case _ =>
        writeComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
        writeLine()
    }
    tryWithHandler.finallyp.foreach(finallyTraverser.traverse)
  }
}
