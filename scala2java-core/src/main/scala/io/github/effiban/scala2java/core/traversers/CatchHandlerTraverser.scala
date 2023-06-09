package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}
import io.github.effiban.scala2java.core.renderers.CatchArgumentRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Case

trait CatchHandlerTraverser {
  def traverse(catchCase: Case,
               context: CatchHandlerContext = CatchHandlerContext()): Unit
}

private[traversers] class CatchHandlerTraverserImpl(catchArgumentTraverser: => CatchArgumentTraverser,
                                                    catchArgumentRenderer: => CatchArgumentRenderer,
                                                    blockTraverser: => BlockTraverser)
                                                   (implicit javaWriter: JavaWriter) extends CatchHandlerTraverser {

  import javaWriter._

  override def traverse(catchCase: Case,
                        context: CatchHandlerContext = CatchHandlerContext()): Unit = {
    write("catch ")
    val traversedArg = catchArgumentTraverser.traverse(catchCase.pat)
    catchArgumentRenderer.render(traversedArg)
    blockTraverser.traverse(catchCase.body, context = BlockContext(shouldReturnValue = context.shouldReturnValue))
  }
}
