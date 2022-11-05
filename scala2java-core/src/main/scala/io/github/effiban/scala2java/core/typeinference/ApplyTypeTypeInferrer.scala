package io.github.effiban.scala2java.core.typeinference

import scala.meta.{Term, Type}

trait ApplyTypeTypeInferrer extends TypeInferrer[Term.ApplyType]

private[typeinference] class ApplyTypeTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends ApplyTypeTypeInferrer {

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    termTypeInferrer.infer(termApplyType.fun)
      .map(mainType => Type.Apply(mainType, termApplyType.targs))
  }
}