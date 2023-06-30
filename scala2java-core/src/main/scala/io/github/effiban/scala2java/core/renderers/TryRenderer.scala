package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, TryRenderContext}
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
    validateContext(`try`, context)
    write("try")
    renderBody(`try`.expr, context.exprContext)
    `try`.catchp.zipWithIndex.foreach { case (catchCase, idx) =>
      catchHandlerRenderer.render(
        catchCase = catchCase,
        context = context.catchContexts(idx)
      )
    }
    `try`.finallyp.foreach(finallyRenderer.render)
  }

  private def validateContext(`try`: Term.Try, context: TryRenderContext): Unit = {
    if (`try`.catchp.length != context.catchContexts.length) {
      throw new IllegalStateException(s"The input 'Try' has ${`try`.catchp.length} 'catch' clauses," +
        s"while the input context has ${context.catchContexts.length} catch contexts")
    }
  }


  private def renderBody(tryExpr: Term, context: BlockRenderContext): Unit = {
    val tryBlock = tryExpr match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("A try expression must be converted to a Block before this point")
    }
    blockRenderer.render(tryBlock, context)
  }

}
