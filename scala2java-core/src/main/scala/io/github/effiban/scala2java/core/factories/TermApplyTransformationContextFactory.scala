package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.typeinference.InternalApplyDeclDefInferrer
import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext

import scala.meta.Term

trait TermApplyTransformationContextFactory {
  def create(termApply: Term.Apply): TermApplyTransformationContext
}

private[factories] class TermApplyTransformationContextFactoryImpl(
    termApplyInferenceContextFactory: => TermApplyInferenceContextFactory,
    applyDeclDefInferrer: => InternalApplyDeclDefInferrer) extends TermApplyTransformationContextFactory {

  override def create(termApply: Term.Apply): TermApplyTransformationContext = {
    val inferenceContext = termApplyInferenceContextFactory.create(termApply)
    val partialDeclDef = applyDeclDefInferrer.infer(termApply, inferenceContext)
    // TODO - infer the applied types when not explicitly provided and add to the context
    TermApplyTransformationContext(
      inferenceContext.maybeParentType,
      partialDeclDef)
  }
}
