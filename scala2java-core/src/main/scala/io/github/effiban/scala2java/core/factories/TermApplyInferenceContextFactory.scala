package io.github.effiban.scala2java.core.factories

import io.github.effiban.scala2java.core.typeinference.{ApplyParentTypeInferrer, TermTypeInferrer}
import io.github.effiban.scala2java.spi.contexts.TermApplyInferenceContext

import scala.meta.{Term, Type}

trait TermApplyInferenceContextFactory {
  def create(termApply: Term.Apply, additionalMaybeArgTypeLists: List[List[Option[Type]]] = Nil): TermApplyInferenceContext
}

private[factories] class TermApplyInferenceContextFactoryImpl(applyParentTypeInferrer: => ApplyParentTypeInferrer,
                                                              termTypeInferrer: => TermTypeInferrer) extends TermApplyInferenceContextFactory {

  override def create(termApply: Term.Apply, additionalMaybeArgTypeLists: List[List[Option[Type]]] = Nil): TermApplyInferenceContext = {
    val maybeParentType = applyParentTypeInferrer.infer(termApply)
    val maybeArgTypeLists = List(termApply.args.map(termTypeInferrer.infer)) ++ additionalMaybeArgTypeLists
    TermApplyInferenceContext(maybeParentType, maybeArgTypeLists)
  }
}