package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.contexts.{BlockRenderContext2, BlockStatRenderContext2}
import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Stat
import scala.meta.Term.Block

trait BlockRenderer {

  def render(block: Block, context: BlockRenderContext2 = BlockRenderContext2()): Unit
}

private[renderers] class BlockRendererImpl(blockStatRenderer: => BlockStatRenderer)
                                          (implicit javaWriter: JavaWriter) extends BlockRenderer {

  import javaWriter._

  override def render(block: Block, context: BlockRenderContext2 = BlockRenderContext2()): Unit = {
    writeBlockStart()
    renderContents(block.stats, context)
    writeBlockEnd()
  }

  private def renderContents(stats: List[Stat], context: BlockRenderContext2): Unit = {
    if (stats.nonEmpty) {
      stats.slice(0, stats.length - 1).foreach(blockStatRenderer.render)
      blockStatRenderer.renderLast(stats.last, BlockStatRenderContext2(context.uncertainReturn))
    }
  }
}
