package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait DefaultTermRefTraverser extends TermRefTraverser1

private[traversers] class DefaultTermRefTraverserImpl(defaultTermSelectTraverser: => DefaultTermSelectTraverser)
  extends DefaultTermRefTraverser {


  override def traverse(termRef: Term.Ref): Term.Ref = termRef match {
    case termSelect: Term.Select => defaultTermSelectTraverser.traverse(termSelect)
    case aTermRef => aTermRef
  }
}
