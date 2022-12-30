package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.ApplyTypeTypeInferrer

import scala.meta.{Term, Type}

private[typeinference] class CoreApplyTypeTypeInferrer(termTypeInferrer: => TermTypeInferrer) extends ApplyTypeTypeInferrer {

  override def infer(termApplyType: Term.ApplyType): Option[Type] = {
    termTypeInferrer.infer(termApplyType.fun)
      .map(mainType => Type.Apply(mainType, termApplyType.targs))
  }
}