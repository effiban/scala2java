package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emitComment

import scala.meta.Term
import scala.meta.Term.{ApplyUnary, Super, This}

object TermRefTraverser extends ScalaTreeTraverser[Term.Ref] {

  override def traverse(termRef: Term.Ref): Unit = termRef match {
    case `this`: This => ThisTraverser.traverse(`this`)
    case `super`: Super => SuperTraverser.traverse(`super`)
    case termName: Term.Name => TermNameTraverser.traverse(termName)
    case termSelect: Term.Select => TermSelectTraverser.traverse(termSelect)
    case applyUnary: ApplyUnary => ApplyUnaryTraverser.traverse(applyUnary)
    case _ => emitComment(s"UNSUPPORTED: $termRef")
  }
}
