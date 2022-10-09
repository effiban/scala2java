package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.ForYieldToTermApplyTransformer

import scala.meta.Term.ForYield

trait ForYieldTraverser extends ScalaTreeTraverser[ForYield]

private[traversers] class ForYieldTraverserImpl(termApplyTraverser: => TermApplyTraverser,
                                                forYieldToTermApplyTransformer: ForYieldToTermApplyTransformer) extends ForYieldTraverser {

  override def traverse(forYield: ForYield): Unit = {
    termApplyTraverser.traverse(forYieldToTermApplyTransformer.transform(forYield.enums, forYield.body))
  }
}
