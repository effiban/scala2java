package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait TryTraverser {
  def traverse(`try`: Term.Try, context: TryContext = TryContext()): Unit
}

private[traversers] class TryTraverserImpl(blockTraverser: => BlockTraverser,
                                           catchHandlerTraverser: => CatchHandlerTraverser,
                                           finallyTraverser: => FinallyTraverser)
                                          (implicit javaWriter: JavaWriter) extends TryTraverser {

  import javaWriter._

  // TODO Support case condition by moving into body
  override def traverse(`try`: Term.Try, context: TryContext = TryContext()): Unit = {
    write("try")
    blockTraverser.traverse(`try`.expr, context = BlockContext(shouldReturnValue = context.shouldReturnValue))
    `try`.catchp.foreach(`case` =>
      catchHandlerTraverser.traverse(
        catchCase = `case`,
        context = CatchHandlerContext(shouldReturnValue = context.shouldReturnValue)
      )
    )
    `try`.finallyp.foreach(finallyTraverser.traverse)
  }
}
