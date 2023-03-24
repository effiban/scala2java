package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.factories.TermApplyInferenceContextFactory
import io.github.effiban.scala2java.spi.typeinferrers.{ApplyDeclDefInferrer, TypeInferrer0}

import scala.meta.{Term, Type}

trait ApplyReturnTypeInferrer extends TypeInferrer0[Term.Apply]

private[typeinference] class ApplyReturnTypeInferrerImpl(inferenceContextFactory: => TermApplyInferenceContextFactory,
                                                         applyDeclDefInferrer: => ApplyDeclDefInferrer) extends ApplyReturnTypeInferrer {

  override def infer(termApply: Term.Apply): Option[Type] = {
    val inferenceContext = inferenceContextFactory.create(termApply)
    applyDeclDefInferrer.infer(termApply, inferenceContext).maybeReturnType
  }
}
