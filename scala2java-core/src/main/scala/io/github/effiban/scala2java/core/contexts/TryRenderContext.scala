package io.github.effiban.scala2java.core.contexts

case class TryRenderContext(exprContext: BlockRenderContext = BlockRenderContext(),
                            catchContexts: List[BlockRenderContext] = Nil) extends BlockStatRenderContext
