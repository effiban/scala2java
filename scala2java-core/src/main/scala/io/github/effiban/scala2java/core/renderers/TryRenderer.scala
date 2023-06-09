package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, CatchHandlerRenderContext, TryRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait TryRenderer {
  def render(`try`: Term.Try, context: TryRenderContext = TryRenderContext()): Unit
}

private[renderers] class TryRendererImpl(blockRenderer: => BlockRenderer,
                                         catchHandlerRenderer: => CatchHandlerRenderer,
                                         finallyRenderer: => FinallyRenderer)
                                        (implicit javaWriter: JavaWriter) extends TryRenderer {

  import javaWriter._

  override def render(`try`: Term.Try, context: TryRenderContext = TryRenderContext()): Unit = {
    write("try")
    renderBody(`try`.expr, context)
    `try`.catchp.foreach(`case` =>
      catchHandlerRenderer.render(
        catchCase = `case`,
        context = CatchHandlerRenderContext(uncertainReturn = context.uncertainReturn)
      )
    )
    `try`.finallyp.foreach(finallyRenderer.render)
  }

  private def renderBody(tryExpr: Term, context: TryRenderContext): Unit = {
    val tryBlock = tryExpr match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("A try expression must be converted to a Block before this point")
    }
    blockRenderer.render(tryBlock, context = BlockRenderContext(uncertainReturn = context.uncertainReturn))
  }

}
