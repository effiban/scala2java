package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.predicates.TermSelectSupportsNoArgInvocation
import io.github.effiban.scala2java.spi.typeinferrers.{SelectTypeInferrer, TypeInferrer0}

import scala.meta.{Term, Type}

trait InternalSelectTypeInferrer extends TypeInferrer0[Term.Select]

private[typeinference] class InternalSelectTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer,
                                                            qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            selectTypeInferrer: => SelectTypeInferrer,
                                                            termSelectSupportsNoArgInvocation: TermSelectSupportsNoArgInvocation)
  extends InternalSelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    val maybeQualType = qualifierTypeInferrer.infer(termSelect)
    val inferenceContext = TermSelectInferenceContext(maybeQualType)
    if (termSelectSupportsNoArgInvocation(termSelect, inferenceContext)) {
      applyReturnTypeInferrer.infer(Term.Apply(termSelect, Nil))
    } else {
      selectTypeInferrer.infer(termSelect, inferenceContext)
    }
  }
}