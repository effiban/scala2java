package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

/** Traverser for terms which are stats in a block or template */
trait StatTermTraverser extends ScalaTreeTraverser[Term]

private[traversers] class StatTermTraverserImpl(defaultTermTraverser: => DefaultTermTraverser) extends StatTermTraverser {

  override def traverse(fun: Term): Unit = {
    // TODO handle special cases to prevent infinite recursion of desugaring
    defaultTermTraverser.traverse(fun)
  }
}
