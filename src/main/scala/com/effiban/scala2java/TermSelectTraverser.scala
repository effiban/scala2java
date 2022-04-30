package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term
import scala.meta.Term.Select

trait TermSelectTraverser extends ScalaTreeTraverser[Term.Select]

private[scala2java] class TermSelectTraverserImpl(termTraverser: => TermTraverser,
                                                  termNameTraverser: => TermNameTraverser,
                                                  termRefTraverser: => TermRefTraverser) extends TermSelectTraverser {

  // qualified name
  override def traverse(termSelect: Term.Select): Unit = {
    val adjustedTermRef = termSelect match {
      case Select(Select(Term.Name("scala"), Term.Name("util")), name) => name
      case Select(Select(Term.Name("scala"), Term.Name("package")), name) => name
      case Select(Select(Term.Name("scala"), Term.Name("Predef")), name) => name
      case Select(Select(Term.Name("_root_"), Term.Name("scala")), name) => name
      case Select(Term.Name("scala"), name) => name
      case _ => termSelect
    }

    adjustedTermRef match {
      case select: Select =>
        termTraverser.traverse(select.qual)
        emit(".")
        termNameTraverser.traverse(select.name)
      case name: Term.Name => termNameTraverser.traverse(name)
      case termRef: Term.Ref => termRefTraverser.traverse(termRef)
    }
  }

}

object TermSelectTraverser extends TermSelectTraverserImpl(
  TermTraverser,
  TermNameTraverser,
  TermRefTraverser
)
