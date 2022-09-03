package effiban.scala2java.traversers

import effiban.scala2java.entities.EnclosingDelimiter.Parentheses
import effiban.scala2java.entities.ListTraversalOptions

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

private[traversers] class TermTupleTraverserImpl(termListTraverser: => TermListTraverser) extends TermTupleTraverser {

  // Java supports tuples only in lambdas AFAIK, but the replacement is not obvious - so rendering it always
  override def traverse(termTuple: Term.Tuple): Unit = {
    val options = ListTraversalOptions(maybeEnclosingDelimiter = Some(Parentheses), onSameLine = true)
    termListTraverser.traverse(termTuple.args, options)
  }
}
