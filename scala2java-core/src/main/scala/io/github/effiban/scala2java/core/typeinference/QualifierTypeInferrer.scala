package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Term, Type}

trait QualifierTypeInferrer extends TypeInferrer0[Term.Select]

private[typeinference] class QualifierTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends QualifierTypeInferrer {

  override def infer(termSelect: Term.Select): Option[Type] = {
    termTypeInferrer.infer(termSelect.qual)
  }
}