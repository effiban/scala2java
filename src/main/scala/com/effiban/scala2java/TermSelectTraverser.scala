package com.effiban.scala2java

import scala.meta.Term
import scala.meta.Term.Select

trait TermSelectTraverser extends ScalaTreeTraverser[Term.Select]

private[scala2java] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser)
                                                 (implicit javaEmitter: JavaEmitter) extends TermSelectTraverser {

  import javaEmitter._

  // qualified name
  override def traverse(termSelect: Term.Select): Unit = {
    termSelect match {
      case Select(Term.Name("scala"), name) => termNameTraverser.traverse(name)
      case select =>
        termTraverser.traverse(select.qual)
        emit(".")
        termNameTraverser.traverse(select.name)
    }
  }

}

object TermSelectTraverser extends TermSelectTraverserImpl(
  TermTraverser,
  TermNameTraverser
)
