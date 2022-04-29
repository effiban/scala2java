package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

object TermApplyInfixTraverser extends TermApplyInfixTraverser {

  // Infix method invocation, e.g.: a + b
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    // TODO - verify implementation for multiple RHS args
    TermTraverser.traverse(termApplyInfix.lhs)
    emit(" ")
    TermNameTraverser.traverse(termApplyInfix.op)
    emit(" ")
    TermListTraverser.traverse(termApplyInfix.args, onSameLine = true)
  }
}
