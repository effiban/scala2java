package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, TryContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.TryWithHandler

@deprecated
trait DeprecatedTryWithHandlerTraverser {
  def traverse(tryWithHandler: TryWithHandler, context: TryContext = TryContext()): Unit
}

@deprecated
private[traversers] class DeprecatedTryWithHandlerTraverserImpl(blockTraverser: => DeprecatedBlockTraverser,
                                                                finallyTraverser: => DeprecatedFinallyTraverser)
                                                               (implicit javaWriter: JavaWriter) extends DeprecatedTryWithHandlerTraverser {

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
