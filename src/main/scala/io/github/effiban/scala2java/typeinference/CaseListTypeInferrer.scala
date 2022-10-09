package io.github.effiban.scala2java.typeinference

import scala.meta.{Case, Type}

trait CaseListTypeInferrer extends TypeInferrer[List[Case]]

private[typeinference] class CaseListTypeInferrerImpl(caseTypeInferrer: => CaseTypeInferrer,
                                                      collectiveTypeInferrer: CollectiveTypeInferrer) extends CaseListTypeInferrer {

  override def infer(cases: List[Case]): Option[Type] = {
    val maybeCaseTypes = cases.map(caseTypeInferrer.infer)
    collectiveTypeInferrer.inferScalar(maybeCaseTypes)
  }
}
