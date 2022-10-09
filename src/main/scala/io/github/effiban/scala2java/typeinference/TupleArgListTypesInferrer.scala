package io.github.effiban.scala2java.typeinference

import scala.meta.{Term, Type}

trait TupleArgListTypesInferrer {
  def infer(terms: List[Term.Tuple]): List[Type]
}

private[typeinference] class TupleArgListTypesInferrerImpl(tupleTypeInferrer: => TupleTypeInferrer,
                                                           collectiveTypeInferrer: CollectiveTypeInferrer)
  extends TupleArgListTypesInferrer {

  override def infer(tupleArgs: List[Term.Tuple]): List[Type] = {
    collectiveTypeInferrer.inferTuple(tupleArgs.map(tupleTypeInferrer.infer)).args
  }
}