package io.github.effiban.scala2java.core.typeinference

import scala.meta.{Term, Type}

trait ScalarArgListTypeInferrer {
  def infer(args: List[Term]): Type
}

private[typeinference] class ScalarArgListTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer,
                                                           collectiveTypeInferrer: CollectiveTypeInferrer)
  extends ScalarArgListTypeInferrer {

  override def infer(args: List[Term]): Type = {
    collectiveTypeInferrer.inferScalar(args.map(termTypeInferrer.infer)).getOrElse(Type.Name("Any"))
  }
}