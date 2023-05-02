package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

private[traversers] class FunTermRefTraverser(funTermSelectTraverser: => FunTermSelectTraverser,
                                              defaultTermRefTraverser: => DefaultTermRefTraverser) extends TermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case termSelect: Term.Select => funTermSelectTraverser.traverse(termSelect)
    case aTermRef => defaultTermRefTraverser.traverse(aTermRef)
  }
}
