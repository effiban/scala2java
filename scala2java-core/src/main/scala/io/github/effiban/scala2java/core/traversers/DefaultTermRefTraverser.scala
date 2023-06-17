package io.github.effiban.scala2java.core.traversers

import scala.meta.Term
import scala.meta.Term.{Super, This}

trait DefaultTermRefTraverser extends TermRefTraverser

private[traversers] class DefaultTermRefTraverserImpl(thisTraverser: ThisTraverser,
                                                      superTraverser: SuperTraverser,
                                                      defaultTermSelectTraverser: => DefaultTermSelectTraverser)
  extends DefaultTermRefTraverser {


  override def traverse(termRef: Term.Ref): Term.Ref = termRef match {
    case `this`: This => thisTraverser.traverse(`this`)
    case `super`: Super => superTraverser.traverse(`super`)
    case termSelect: Term.Select => defaultTermSelectTraverser.traverse(termSelect)
    case aTermRef => aTermRef
  }
}
