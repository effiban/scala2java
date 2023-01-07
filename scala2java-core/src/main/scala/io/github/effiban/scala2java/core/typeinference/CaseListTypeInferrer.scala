package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.{Case, Type}

trait CaseListTypeInferrer extends TypeInferrer0[List[Case]]

private[typeinference] class CaseListTypeInferrerImpl(caseTypeInferrer: => CaseTypeInferrer,
                                                      collectiveTypeInferrer: CollectiveTypeInferrer) extends CaseListTypeInferrer {

  override def infer(cases: List[Case]): Option[Type] = {
    val maybeCaseTypes = cases.map(caseTypeInferrer.infer)
    collectiveTypeInferrer.inferScalar(maybeCaseTypes)
  }
}
