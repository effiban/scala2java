package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term
import scala.meta.Term.{ApplyUnary, Super, This}

trait TermRefTraverser extends ScalaTreeTraverser[Term.Ref]

private[scala2java] class TermRefTraverserImpl(thisTraverser: => ThisTraverser,
                                               superTraverser: => SuperTraverser,
                                               termNameTraverser: => TermNameTraverser,
                                               termSelectTraverser: => TermSelectTraverser,
                                               applyUnaryTraverser: => ApplyUnaryTraverser)
                                              (implicit javaEmitter: JavaEmitter) extends TermRefTraverser {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case `this`: This => thisTraverser.traverse(`this`)
    case `super`: Super => superTraverser.traverse(`super`)
    case termName: Term.Name => termNameTraverser.traverse(termName)
    case termSelect: Term.Select => termSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => applyUnaryTraverser.traverse(applyUnary)
    case _ => emitComment(s"UNSUPPORTED: $termRef")
  }
}

object TermRefTraverser extends TermRefTraverserImpl(
  ThisTraverser,
  SuperTraverser,
  TermNameTraverser,
  TermSelectTraverser,
  ApplyUnaryTraverser
)