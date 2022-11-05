package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.writers.JavaWriter

import scala.meta.Term
import scala.meta.Term.{ApplyUnary, Super, This}

trait TermRefTraverser extends ScalaTreeTraverser[Term.Ref]

private[traversers] class TermRefTraverserImpl(thisTraverser: => ThisTraverser,
                                               superTraverser: => SuperTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termSelectTraverser: => TermSelectTraverser,
                                               applyUnaryTraverser: => ApplyUnaryTraverser)
                                              (implicit javaWriter: JavaWriter) extends TermRefTraverser {

  import javaWriter._

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case `this`: This => thisTraverser.traverse(`this`)
    case `super`: Super => superTraverser.traverse(`super`)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => termSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case _ => writeComment(s"UNSUPPORTED: $termRef")
  }
}
