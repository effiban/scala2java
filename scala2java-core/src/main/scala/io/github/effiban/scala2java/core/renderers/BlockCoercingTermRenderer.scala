package io.github.effiban.scala2java.core.renderers

import io.github.effiban.scala2java.core.renderers.contexts.BlockRenderContext

import scala.meta.Term
import scala.meta.Term.Block

trait BlockCoercingTermRenderer {
  def render(term: Term, context: BlockRenderContext = BlockRenderContext()): Unit
}

private[renderers] class BlockCoercingTermRendererImpl(blockRenderer: => BlockRenderer) extends BlockCoercingTermRenderer {

  override def render(term: Term, context: BlockRenderContext = BlockRenderContext()): Unit = {
    val block = term match {
      case aBlock: Block => aBlock
      case _ => throw new IllegalStateException(s"The term $term should have been converted to a Block before this point")
    }
    blockRenderer.render(block, context)
  }
}
