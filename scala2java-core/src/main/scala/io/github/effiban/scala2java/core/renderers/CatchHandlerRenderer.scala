package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, CatchHandlerRenderContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term.Block
import scala.meta.{Case, Term}

trait CatchHandlerRenderer {
  def render(catchCase: Case,
             context: CatchHandlerRenderContext = CatchHandlerRenderContext()): Unit
}

private[renderers] class CatchHandlerRendererImpl(catchArgumentRenderer: => CatchArgumentRenderer,
                                                  blockRenderer: => BlockRenderer)
                                                 (implicit javaWriter: JavaWriter) extends CatchHandlerRenderer {

  import javaWriter._

  override def render(catchCase: Case,
                      context: CatchHandlerRenderContext = CatchHandlerRenderContext()): Unit = {
    write("catch ")
    catchArgumentRenderer.render(catchCase.pat)
    renderBody(catchCase.body, context)
  }

  private def renderBody(body: Term, context: CatchHandlerRenderContext): Unit = {
    val block = body match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException("The body of a catch clause must be converted to a Block before this point")
    }

    blockRenderer.render(block, context = BlockRenderContext(uncertainReturn = context.uncertainReturn))
  }
}
