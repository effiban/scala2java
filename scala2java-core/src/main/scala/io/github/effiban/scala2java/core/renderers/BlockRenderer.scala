package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext, InitContext}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Stat
import scala.meta.Term.Block

trait BlockRenderer {

  def render(block: Block, context: BlockRenderContext = BlockRenderContext()): Unit
}

private[renderers] class BlockRendererImpl(blockStatRenderer: => BlockStatRenderer,
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
    renderContents(block.stats, uncertainReturn)
    writeBlockEnd()
  }

  private def renderContents(stats: List[Stat], uncertainReturnValue: Boolean): Unit = {
    if (stats.nonEmpty) {
      stats.slice(0, stats.length - 1).foreach(blockStatRenderer.render)
      blockStatRenderer.renderLast(stats.last, uncertainReturnValue)
    }
  }
}
