package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.Block

trait FinallyTraverser extends ScalaTreeTraverser2[Term, Block]

private[traversers] class FinallyTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser) extends FinallyTraverser {

  // TODO support return value flag
  override def traverse(finallyp: Term): Block = {
    blockWrappingTermTraverser.traverse(finallyp)
  }
}
