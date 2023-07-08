package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext2, TryRenderContext2}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{Block, TryWithHandler}

trait TryWithHandlerRenderer {
  def render(tryWithHandler: TryWithHandler, context: TryRenderContext2 = TryRenderContext2()): Unit
}

private[renderers] class TryWithHandlerRendererImpl(blockRenderer: => BlockRenderer,
                                                    finallyRenderer: => FinallyRenderer)
                                                   (implicit javaWriter: JavaWriter) extends TryWithHandlerRenderer {

  import javaWriter._

  override def render(tryWithHandler: TryWithHandler, context: TryRenderContext2 = TryRenderContext2()): Unit = {
    write("try")
    renderBody(tryWithHandler.expr, BlockRenderContext2(context.uncertainReturn))
    // The catch handler is some term which evaluates to a partial function, which we cannot handle (without semantic information)
    writeComment(s"UNPARSEABLE catch handler: ${tryWithHandler.catchp}")
    writeLine()

    tryWithHandler.finallyp.foreach(finallyRenderer.render)
  }

  private def renderBody(tryExpr: Term, context: BlockRenderContext2): Unit = {
    val tryBlock = tryExpr match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("A try expression must be converted to a Block before this point")
    }
    blockRenderer.render(tryBlock, context)
  }
}
