package io.github.effiban.scala2java.core.typeinference

import io.github.effiban.scala2java.spi.typeinferrers.TypeInferrer0

import scala.meta.Term.Try
import scala.meta.Type

trait TryTypeInferrer extends TypeInferrer0[Try]

private[typeinference] class TryTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer,
                                                 caseListTypeInferrer: => CaseListTypeInferrer,
                                                 collectiveTypeInferrer: CollectiveTypeInferrer) extends TryTypeInferrer {

  override def infer(`try`: Try): Option[Type] = {
    val maybeExprType = termTypeInferrer.infer(`try`.expr)
    val maybeCatchType = caseListTypeInferrer.infer(`try`.catchp)
    val maybeFinallyType = `try`.finallyp match {
      case Some(finallyp) => termTypeInferrer.infer(finallyp)
      case None => Some(Type.AnonymousName())
    }
    collectiveTypeInferrer.inferScalar(List(maybeExprType, maybeCatchType, maybeFinallyType))
  }
}
