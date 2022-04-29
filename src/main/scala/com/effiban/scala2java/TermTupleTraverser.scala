package com.effiban.scala2java

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

object TermTupleTraverser extends TermTupleTraverser {

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it always
  override def traverse(termTuple: Term.Tuple): Unit = {
    TermListTraverser.traverse(termTuple.args, maybeDelimiterType = Some(Parentheses), onSameLine = true)
  }
}
