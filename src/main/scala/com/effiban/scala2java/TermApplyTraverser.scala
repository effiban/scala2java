package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.Parentheses

import scala.meta.Term

object TermApplyTraverser extends ScalaTreeTraverser[Term.Apply] {

  // method invocation
  override def traverse(termApply: Term.Apply): Unit = {
    TermTraverser.traverse(termApply.fun)
    TermListTraverser.traverse(termApply.args, maybeDelimiterType = Some(Parentheses))
  }
}
