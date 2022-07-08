package effiban.scala2java.traversers

import effiban.scala2java.Parentheses

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

private[scala2java] class TermTupleTraverserImpl(termListTraverser: => TermListTraverser) extends TermTupleTraverser {

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it always
  override def traverse(termTuple: Term.Tuple): Unit = {
    termListTraverser.traverse(termTuple.args, maybeDelimiterType = Some(Parentheses), onSameLine = true)
  }
}

object TermTupleTraverser extends TermTupleTraverserImpl(TermListTraverser)