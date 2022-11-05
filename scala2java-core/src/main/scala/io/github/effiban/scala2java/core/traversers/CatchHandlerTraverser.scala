package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext, StatContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term

trait CatchHandlerTraverser {
  def traverse(param: Term.Param,
               body: Term,
               context: CatchHandlerContext = CatchHandlerContext()): Unit
}

private[traversers] class CatchHandlerTraverserImpl(termParamListTraverser: => TermParamListTraverser,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends CatchHandlerTraverser {

  import javaWriter._

  override def traverse(param: Term.Param,
                        body: Term,
                        context: CatchHandlerContext = CatchHandlerContext()): Unit = {
    write("catch ")
    termParamListTraverser.traverse(
      termParams = List(param),
      // TODO - consider adding a Java scope type for the catch handler
      context = StatContext(),
      onSameLine = true
    )
    blockTraverser.traverse(body, context = BlockContext(shouldReturnValue = context.shouldReturnValue))
  }
}
