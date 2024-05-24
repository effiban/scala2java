package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.typeinference.InternalApplyDeclDefInferrer
import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext

import scala.meta.Term

trait UnqualifiedTermApplyTransformationContextFactory {
  def create(termApply: Term.Apply): UnqualifiedTermApplyTransformationContext
}

private[factories] class UnqualifiedTermApplyTransformationContextFactoryImpl(
    termApplyInferenceContextFactory: => TermApplyInferenceContextFactory,
    applyDeclDefInferrer: => InternalApplyDeclDefInferrer) extends UnqualifiedTermApplyTransformationContextFactory {

  override def create(termApply: Term.Apply): UnqualifiedTermApplyTransformationContext = {
    val inferenceContext = termApplyInferenceContextFactory.create(termApply)
    val partialDeclDef = applyDeclDefInferrer.infer(termApply, inferenceContext)
    // TODO - infer the applied types when not explicitly provided and add to the context
    UnqualifiedTermApplyTransformationContext(
      inferenceContext.maybeParentType,
      partialDeclDef)
  }
}
