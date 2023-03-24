package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Term, Type}

trait ApplyParentTypeInferrer extends TypeInferrer0[Term.Apply]

private[typeinference] class ApplyParentTypeInferrerImpl(qualifierTypeInferrer: => QualifierTypeInferrer) extends ApplyParentTypeInferrer {

  override def infer(termApply: Term.Apply): Option[Type] = {
    termApply.fun match {
      case termSelect: Term.Select => qualifierTypeInferrer.infer(termSelect)
      case Term.ApplyType(termSelect: Term.Select, _) => qualifierTypeInferrer.infer(termSelect)
      case _ => None
    }
  }
}