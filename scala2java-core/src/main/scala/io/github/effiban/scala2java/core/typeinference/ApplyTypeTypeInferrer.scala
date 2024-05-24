package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaClass

import scala.meta.{Term, Type, XtensionQuasiquoteTerm}

trait ApplyTypeTypeInferrer {
  def infer(termApplyType: Term.ApplyType): Option[Type]
}

private[typeinference] class ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer)
  extends ApplyTypeTypeInferrer {

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    termApplyType match {
      case Term.ApplyType(q"classOf", List(tpe)) => Some(Type.Apply(ScalaClass, List(tpe)))
      case aTermApplyType => applyReturnTypeInferrer.infer(Term.Apply(aTermApplyType, Nil))
    }
  }
}