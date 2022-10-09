package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.TermTupleToTermApplyTransformer

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

private[traversers] class TermTupleTraverserImpl(termApplyTraverser: => TermApplyTraverser,
                                                 termTupleToTermApplyTransformer: TermTupleToTermApplyTransformer)
  extends TermTupleTraverser {

  override def traverse(termTuple: Term.Tuple): Unit = {
    termApplyTraverser.traverse(termTupleToTermApplyTransformer.transform(termTuple))
  }
}
