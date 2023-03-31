package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues.Apply
import io.github.effiban.scala2java.spi.predicates.TermNameHasApplyMethod

import scala.meta.{Term, Type}

trait ApplyTypeTypeInferrer {
  def infer(termApplyType: Term.ApplyType): Option[Type]
}

private[typeinference] class ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer,
                                                       termNameHasApplyMethod: TermNameHasApplyMethod)
  extends ApplyTypeTypeInferrer {

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    termApplyType match {
      case Term.ApplyType(termName: Term.Name, targs) if termNameHasApplyMethod(termName) =>
        applyReturnTypeInferrer.infer(Term.Apply(
          Term.ApplyType(Term.Select(termName, Term.Name(Apply)), targs),
          Nil))
      case aTermApplyType => applyReturnTypeInferrer.infer(Term.Apply(aTermApplyType, Nil))
    }
  }
}