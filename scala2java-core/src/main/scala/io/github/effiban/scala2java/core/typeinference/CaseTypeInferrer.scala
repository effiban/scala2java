package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Case, Type}

trait CaseTypeInferrer extends TypeInferrer0[Case]

private[typeinference] class CaseTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends CaseTypeInferrer {

  override def infer(`case`: Case): Option[Type] = termTypeInferrer.infer(`case`.body)
}
