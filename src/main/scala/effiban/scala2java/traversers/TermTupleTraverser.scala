package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

private[traversers] class TermTupleTraverserImpl(termListTraverser: => TermListTraverser) extends TermTupleTraverser {

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it always
  override def traverse(termTuple: Term.Tuple): Unit = {
    termListTraverser.traverse(termTuple.args, maybeEnclosingDelimiter = Some(Parentheses), onSameLine = true)
  }
}
