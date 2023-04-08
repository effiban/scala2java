package io.github.effiban.scala2java.core.transformers

import io.github.effiban.scala2java.spi.predicates.TermNameSupportsNoArgInvocation

import scala.meta.Term

private[transformers] class EvaluatedInternalTermNameTransformer(defaultInternalTermNameTransformer: => InternalTermNameTransformer,
                                                                 termNameSupportsNoArgInvocation: TermNameSupportsNoArgInvocation)
  extends InternalTermNameTransformer {

  override def transform(termName: Term.Name): Term = {
    if (termNameSupportsNoArgInvocation(termName)) {
      Term.Apply(termName, Nil)
    } else {
      defaultInternalTermNameTransformer.transform(termName)
    }
  }
}
