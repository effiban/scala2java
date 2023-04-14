package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.contexts.{TermSelectInferenceContext, TermSelectTransformationContext}
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation

import scala.meta.Term

private[transformers] class EvaluatedInternalTermSelectTransformer(defaultInternalTermSelectTransformer: => InternalTermSelectTransformer,
                                                                   termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation)
  extends InternalTermSelectTransformer {

  override def transform(termSelect: Term.Select, context: TermSelectTransformationContext = TermSelectTransformationContext()): Term = {
    val inferenceContext = TermSelectInferenceContext(context.maybeQualType)
    if (termSelectSupportsNoArgInvocation(termSelect, inferenceContext)) {
      Term.Apply(termSelect, Nil)
    } else {
      defaultInternalTermSelectTransformer.transform(termSelect, context)
    }
  }
}
