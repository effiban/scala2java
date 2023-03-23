package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.{SelectTypeInferrer, TypeInferrer0}

import scala.meta.{Term, Type}

trait InternalSelectTypeInferrer extends TypeInferrer0[Term.Select]

private[typeinference] class InternalSelectTypeInferrerImpl(qualifierTypeInferrer: => QualifierTypeInferrer,
                                                            selectTypeInferrer: => SelectTypeInferrer)
  extends InternalSelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    val maybeQualType = qualifierTypeInferrer.infer(termSelect)
    selectTypeInferrer.infer(termSelect, TermSelectInferenceContext(maybeQualType))
  }
}