package io.github.effiban.scala2java.core.traversers

import io.github.effiban.scala2java.core.transformers.TermTupleToTermApplyTransformer

import scala.meta.Term

@deprecated
trait DeprecatedTermTupleTraverser extends ScalaTreeTraverser[Term.Tuple]

@deprecated
private[traversers] class DeprecatedTermTupleTraverserImpl(termApplyTraverser: => DeprecatedTermApplyTraverser,
                                                           termTupleToTermApplyTransformer: TermTupleToTermApplyTransformer)
  extends DeprecatedTermTupleTraverser {

  override def traverse(termTuple: Term.Tuple): Unit = {
    termApplyTraverser.traverse(termTupleToTermApplyTransformer.transform(termTuple))
  }
}
