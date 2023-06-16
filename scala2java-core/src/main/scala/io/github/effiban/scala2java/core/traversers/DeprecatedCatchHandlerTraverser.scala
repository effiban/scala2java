package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.{BlockContext, CatchHandlerContext}
import io.github.effiban.scala2java.core.renderers.CatchArgumentRenderer
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Case

@deprecated
trait DeprecatedCatchHandlerTraverser {
  def traverse(catchCase: Case,
               context: CatchHandlerContext = CatchHandlerContext()): Unit
}

@deprecated
private[traversers] class DeprecatedCatchHandlerTraverserImpl(catchArgumentTraverser: => CatchArgumentTraverser,
                                                              catchArgumentRenderer: => CatchArgumentRenderer,
                                                              blockTraverser: => DeprecatedBlockTraverser)
                                                             (implicit javaWriter: JavaWriter) extends DeprecatedCatchHandlerTraverser {

  import javaWriter._

  override def traverse(catchCase: Case,
                        context: CatchHandlerContext = CatchHandlerContext()): Unit = {
    write("catch ")
    val traversedArg = catchArgumentTraverser.traverse(catchCase.pat)
    catchArgumentRenderer.render(traversedArg)
    blockTraverser.traverse(catchCase.body, context = BlockContext(shouldReturnValue = context.shouldReturnValue))
  }
}
