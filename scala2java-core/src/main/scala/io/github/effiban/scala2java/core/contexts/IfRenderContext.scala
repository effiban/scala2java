package io.github.effiban.scala2java.core.contexts

case class IfRenderContext(thenContext: BlockRenderContext = BlockRenderContext(),
                           elseContext: BlockRenderContext = BlockRenderContext()) extends BlockStatRenderContext
