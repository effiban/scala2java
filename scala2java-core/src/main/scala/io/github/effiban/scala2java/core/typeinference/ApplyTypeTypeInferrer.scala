package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TermNameValues.ScalaClassOf
import io.github.effiban.scala2java.core.entities.TypeNameValues

import scala.meta.{Term, Type}

trait ApplyTypeTypeInferrer {
  def infer(termApplyType: Term.ApplyType): Option[Type]
}

private[typeinference] class ApplyTypeTypeInferrerImpl(applyReturnTypeInferrer: => ApplyReturnTypeInferrer)
  extends ApplyTypeTypeInferrer {

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    termApplyType match {
      case Term.ApplyType(Term.Name(ScalaClassOf), List(tpe)) => Some(Type.Apply(Type.Name(TypeNameValues.Class), List(tpe)))
      case aTermApplyType => applyReturnTypeInferrer.infer(Term.Apply(aTermApplyType, Nil))
    }
  }
}