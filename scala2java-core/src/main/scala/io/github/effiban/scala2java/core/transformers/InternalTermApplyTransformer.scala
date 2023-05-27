package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext
import io.github.effiban.scala2java.spi.transformers.TermApplyTransformer

import scala.annotation.tailrec
import scala.meta.Term

trait InternalTermApplyTransformer {
  def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply
}

private[transformers] class InternalTermApplyTransformerImpl(termApplyTransformer: TermApplyTransformer) extends InternalTermApplyTransformer {

  @tailrec
  override final def transform(termApply: Term.Apply, context: TermApplyTransformationContext = TermApplyTransformationContext()): Term.Apply = {
    termApply match {
      // Invocation of method with more than one param list
      case Term.Apply(Term.Apply(fun, args1), args2) => transform(Term.Apply(fun, args1 ++ args2), context)
      case other => termApplyTransformer.transform(other, context)
    }
  }
}
