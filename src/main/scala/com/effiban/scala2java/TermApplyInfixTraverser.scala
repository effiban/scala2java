package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.emit

import scala.meta.Term

trait TermApplyInfixTraverser extends ScalaTreeTraverser[Term.ApplyInfix]

private[scala2java] class TermApplyInfixTraverserImpl(termTraverser: => TermTraverser,
                                                      termNameTraverser: => TermNameTraverser,
                                                      termListTraverser: => TermListTraverser) extends TermApplyInfixTraverser {

  // Infix method invocation, e.g.: a + b
  override def traverse(termApplyInfix: Term.ApplyInfix): Unit = {
    // TODO - verify implementation for multiple RHS args
    termTraverser.traverse(termApplyInfix.lhs)
    emit(" ")
    termNameTraverser.traverse(termApplyInfix.op)
    emit(" ")
    termListTraverser.traverse(termApplyInfix.args, onSameLine = true)
  }
}

object TermApplyInfixTraverser extends TermApplyInfixTraverserImpl(TermTraverser, TermNameTraverser, TermListTraverser)
