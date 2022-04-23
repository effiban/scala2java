package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term

object TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix] {

  // Infix method invocation, e.g.: a + b
  def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    // TODO - verify implementation for multiple RHS args
    GenericTreeTraverser.traverse(termApplyInfix.lhs)
    emit(" ")
    GenericTreeTraverser.traverse(termApplyInfix.op)
    emit(" ")
    ArgumentListTraverser.traverse(termApplyInfix.args, onSameLine = true)
  }
}
