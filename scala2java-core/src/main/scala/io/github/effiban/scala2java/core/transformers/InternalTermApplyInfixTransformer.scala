package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

trait InternalTermApplyInfixTransformer {
  def transform(termApplyInfix: Term.ApplyInfix): Term
}

private[transformers] class InternalTermApplyInfixTransformerImpl(termApplyInfixToTermApplyTransformer: TermApplyInfixToTermApplyTransformer,
                                                                  treeTransformer: => TreeTransformer)
  extends InternalTermApplyInfixTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Term = {
    termApplyInfixToTermApplyTransformer.transform(termApplyInfix) match {
      case Some(termApply: Term.Apply) => treeTransformer.transform(termApply).asInstanceOf[Term]
      case None => termApplyInfix.copy(
        lhs = treeTransformer.transform(termApplyInfix.lhs).asInstanceOf[Term],
        args = termApplyInfix.args.map(treeTransformer.transform(_).asInstanceOf[Term])
      )
    }
  }
}
