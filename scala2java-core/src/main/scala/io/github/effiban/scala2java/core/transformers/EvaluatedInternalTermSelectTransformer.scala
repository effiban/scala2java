package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.Term

private[transformers] class EvaluatedInternalTermSelectTransformer(defaultInternalTermSelectTransformer: => InternalTermSelectTransformer,
                                                                   termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation)
  extends InternalTermSelectTransformer {

  override def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Term = {
    if (termSelectSupportsNoArgInvocation(termSelect)) {
      Term.Apply(termSelect, Nil)
    } else {
      defaultInternalTermSelectTransformer.transform(termSelect, context)
    }
  }
}
