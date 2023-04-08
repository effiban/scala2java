package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

/** Traverser for terms which are stats in a block or template */

private[traversers] class StatTermTraverser(defaultTermTraverser: => TermTraverser) extends TermTraverser {

  override def traverse(fun: Term): Unit = {
    // TODO handle special cases to prevent infinite recursion of desugaring
    defaultTermTraverser.traverse(fun)
  }
}
