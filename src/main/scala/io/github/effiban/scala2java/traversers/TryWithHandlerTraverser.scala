package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.contexts.{BlockContext, TryContext}
import io.github.effiban.scala2java.writers.JavaWriter

import scala.meta.Term.TryWithHandler

trait TryWithHandlerTraverser {
  def traverse(tryWithHandler: TryWithHandler, context: TryContext = TryContext()): Unit
}

private[traversers] class TryWithHandlerTraverserImpl(blockTraverser: => BlockTraverser,
                                                      finallyTraverser: => FinallyTraverser)
                                                     (implicit javaWriter: JavaWriter) extends TryWithHandlerTraverser {

  import javaWriter._

  override def traverse(tryWithHandler: TryWithHandler, context: TryContext = TryContext()): Unit = {
    write("try")
    blockTraverser.traverse(stat = tryWithHandler.expr, context = BlockContext(shouldReturnValue = context.shouldReturnValue))
    // The catch handler is some term which evaluates to a partial function, which we cannot handle (without semantic information)
    writeComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
    writeLine()

    tryWithHandler.finallyp.foreach(finallyTraverser.traverse)
  }
}
