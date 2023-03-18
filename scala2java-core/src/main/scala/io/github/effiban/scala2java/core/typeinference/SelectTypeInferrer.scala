package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.contexts.TermSelectInferenceContext
import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Term, Type}

trait SelectTypeInferrer extends TypeInferrer0[Term.Select]

private[typeinference] class SelectTypeInferrerImpl(qualifierTypeInferrer: => QualifierTypeInferrer,
                                                    selectWithContextTypeInferrer: => SelectWithContextTypeInferrer) extends SelectTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    val maybeQualType = qualifierTypeInferrer.infer(termSelect)
    selectWithContextTypeInferrer.infer(termSelect, TermSelectInferenceContext(maybeQualType))
  }
}