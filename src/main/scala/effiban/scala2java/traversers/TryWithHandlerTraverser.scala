package effiban.scala2java.traversers

import effiban.scala2java.writers.JavaWriter

import scala.meta.Term.{Block, TryWithHandler}

trait TryWithHandlerTraverser extends ScalaTreeTraverser[TryWithHandler]

private[traversers] class TryWithHandlerTraverserImpl(blockTraverser: => BlockTraverser,
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
    // The catch handler is some term which evaluates to a partial function, which we cannot handle (without semantic information)
    writeComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
    writeLine()

    tryWithHandler.finallyp.foreach(finallyTraverser.traverse)
  }
}
