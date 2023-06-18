package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TermTupleToTermApplyTransformer

import scala.meta.Term

trait TermTupleTraverser extends ScalaTreeTraverser2[Term.Tuple, Term.Apply]

private[traversers] class TermTupleTraverserImpl(termApplyTraverser: => TermApplyTraverser,
                                                 termTupleToTermApplyTransformer: TermTupleToTermApplyTransformer)
  extends TermTupleTraverser {

  override def traverse(termTuple: Term.Tuple): Term.Apply = {
    termApplyTraverser.traverse(termTupleToTermApplyTransformer.transform(termTuple))
  }
}
