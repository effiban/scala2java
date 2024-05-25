package io.github.effiban.scala2java.core.traversers

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser1[Term.Tuple]

private[traversers] class TermTupleTraverserImpl(expressionTermTraverser: => ExpressionTermTraverser)
  extends TermTupleTraverser {

  override def traverse(termTuple: Term.Tuple): Term.Tuple = {
    termTuple.copy(args = termTuple.args.map(expressionTermTraverser.traverse))
  }
}
