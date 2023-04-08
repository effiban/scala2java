package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

private[traversers] class TermRefOverridingTermTraverser(termRefTraverser: => TermRefTraverser,
                                                         termTraverser: => TermTraverser) extends TermTraverser {

  override def traverse(fun: Term): Unit = fun match {
    case ref: Term.Ref => termRefTraverser.traverse(ref)
    case term => termTraverser.traverse(term)
  }
}
