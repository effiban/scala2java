package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.contexts.BlockContext
import io.github.effiban.scala2java.core.traversers.results.BlockTraversalResult

import scala.meta.Term
import scala.meta.Term.Block

trait BlockWrappingTermTraverser {

  def traverse(term: Term, context: BlockContext = BlockContext()): BlockTraversalResult
}

private[traversers] class BlockWrappingTermTraverserImpl(defaultBlockTraverser: => DefaultBlockTraverser)
  extends BlockWrappingTermTraverser {

  // Traverser for a non-expression term which should be wrapped in a block (if not already a block),
  // either due to Java compiler constraints or Java style.
  override def traverse(term: Term, context: BlockContext = BlockContext()): BlockTraversalResult = {
    val block = term match {
      case aBlock: Block => aBlock
      case aTerm => Block(List(aTerm))
    }
    defaultBlockTraverser.traverse(block, context)
  }
}
