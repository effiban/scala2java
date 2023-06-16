package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, TryContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

@deprecated
trait DeprecatedTryTraverser {
  def traverse(`try`: Term.Try, context: TryContext = TryContext()): Unit
}

@deprecated
private[traversers] class DeprecatedTryTraverserImpl(blockTraverser: => DeprecatedBlockTraverser,
                                                     catchHandlerTraverser: => DeprecatedCatchHandlerTraverser,
                                                     finallyTraverser: => DeprecatedFinallyTraverser)
                                                    (implicit javaWriter: JavaWriter) extends DeprecatedTryTraverser {

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
