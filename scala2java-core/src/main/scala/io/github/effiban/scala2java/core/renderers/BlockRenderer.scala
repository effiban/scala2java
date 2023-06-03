package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, InitContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.Block

trait BlockRenderer {

  def render(block: Block, context: BlockRenderContext = BlockRenderContext()): Unit
}

private[renderers] class BlockRendererImpl(blockTermRenderer: => BlockTermRenderer,
                                           initRenderer: => InitRenderer)
                                          (implicit javaWriter: JavaWriter) extends BlockRenderer {

  import javaWriter._

  override def render(block: Block, context: BlockRenderContext = BlockRenderContext()): Unit = {
    import context._

    writeBlockStart()
    maybeInit.foreach(init => {
      initRenderer.render(init, InitContext(argNameAsComment = true))
      writeStatementEnd()
    })
    renderContents(block, uncertainReturn)
    writeBlockEnd()
  }

  private def renderContents(block: Block, uncertainReturnValue: Boolean): Unit = {
    // TODO handle all stats and not just terms using the BlockStatRenderer when ready
    val terms = block.stats.collect { case term: Term => term }
    if (terms.nonEmpty) {
      terms.slice(0, terms.length - 1).foreach(blockTermRenderer.render)
      blockTermRenderer.renderLast(terms.last, uncertainReturnValue)
    }
  }
}
