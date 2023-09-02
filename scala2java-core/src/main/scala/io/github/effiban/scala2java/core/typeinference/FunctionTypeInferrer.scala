package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.core.entities.TypeSelects.ScalaAny

import scala.meta.{Term, Type}

trait FunctionTypeInferrer {

  def infer(termFunction: Term.Function): Type.Function

}

private[typeinference] class FunctionTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends FunctionTypeInferrer {

  override def infer(termFunction: Term.Function): Type.Function = {
    val paramTypes = termFunction.params.map(_.decltpe.getOrElse(ScalaAny))
    val bodyType = termTypeInferrer.infer(termFunction.body).getOrElse(ScalaAny)
    Type.Function(paramTypes, bodyType)
  }
}
