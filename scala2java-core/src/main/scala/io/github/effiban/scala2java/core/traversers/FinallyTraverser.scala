package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait FinallyTraverser extends ScalaTreeTraverser1[Term]

private[traversers] class FinallyTraverserImpl(blockWrappingTermTraverser: => BlockWrappingTermTraverser) extends FinallyTraverser {

  // TODO support return value flag
  override def traverse(finallyp: Term): Term = {
    blockWrappingTermTraverser.traverse(finallyp).block
  }
}
