package com.effiban.scala2java

import com.effiban.scala2java.JavaEmitter.Parentheses

import scala.meta.Term

object TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple] {

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it always
  def traverse(termTuple: Term.Tuple): Unit = {
    TermListTraverser.traverse(termTuple.args, maybeDelimiterType = Some(Parentheses), onSameLine = true)
  }
}
