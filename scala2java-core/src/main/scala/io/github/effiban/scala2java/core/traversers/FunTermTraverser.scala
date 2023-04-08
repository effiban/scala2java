package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

/** Traverser for terms which are function expressions (the part without the arguments) */
private[traversers] class FunTermTraverser(expressionTermTraverser: => TermTraverser) extends TermTraverser {

  override def traverse(fun: Term): Unit = {
    // TODO handle special cases to prevent infinite recursion of desugaring
    expressionTermTraverser.traverse(fun)
  }
}
