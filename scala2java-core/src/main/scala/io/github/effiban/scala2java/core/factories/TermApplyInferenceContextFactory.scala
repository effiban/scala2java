package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.typeinference.{ApplyParentTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext

import scala.meta.Term

trait TermApplyInferenceContextFactory {
  def create(termApply: Term.Apply): TermApplyInferenceContext
}

private[factories] class TermApplyInferenceContextFactoryImpl(applyParentTypeInferrer: => ApplyParentTypeInferrer,
                                                              termTypeInferrer: => TermTypeInferrer) extends TermApplyInferenceContextFactory {

  override def create(termApply: Term.Apply): TermApplyInferenceContext = {
    val maybeParentType = applyParentTypeInferrer.infer(termApply)
    val maybeArgTypes = termApply.args.map(termTypeInferrer.infer)
    TermApplyInferenceContext(maybeParentType, maybeArgTypes)
  }
}