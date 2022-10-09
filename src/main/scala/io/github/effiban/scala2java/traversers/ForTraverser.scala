package io.github.effiban.scala2java.traversers

import io.github.effiban.scala2java.transformers.ForToTermApplyTransformer

import scala.meta.Term.For

trait ForTraverser extends ScalaTreeTraverser[For]

private[traversers] class ForTraverserImpl(termApplyTraverser: => TermApplyTraverser,
                                           forToTermApplyTransformer: ForToTermApplyTransformer) extends ForTraverser {

  override def traverse(`for`: For): Unit = {
    termApplyTraverser.traverse(forToTermApplyTransformer.transform(`for`.enums, `for`.body))
  }
}
