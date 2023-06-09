package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Block, TryWithHandler}

trait TryWithHandlerRenderer {
  def render(tryWithHandler: TryWithHandler, context: TryRenderContext = TryRenderContext()): Unit
}

private[renderers] class TryWithHandlerRendererImpl(blockRenderer: => BlockRenderer,
                                                    finallyRenderer: => FinallyRenderer)
                                                   (implicit javaWriter: JavaWriter) extends TryWithHandlerRenderer {

  import javaWriter._

  override def render(tryWithHandler: TryWithHandler, context: TryRenderContext = TryRenderContext()): Unit = {
    write("try")
    renderBody(tryWithHandler.expr, context)
    // The catch handler is some term which evaluates to a partial function, which we cannot handle (without semantic information)
    writeComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
    writeLine()

    tryWithHandler.finallyp.foreach(finallyRenderer.render)
  }

  private def renderBody(tryExpr: Term, context: TryRenderContext): Unit = {
    val tryBlock = tryExpr match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("A try expression must be converted to a Block before this point")
    }
    blockRenderer.render(tryBlock, context = BlockRenderContext(uncertainReturn = context.uncertainReturn))
  }
}
