package effiban.scala2java.typeinference

import scala.meta.{Term, Type}

trait TermArgsToTypeArgsInferrer {
  def infer(terms: List[Term]): List[Type]
}

private[typeinference] class TermsToTypeArgsInferrerImpl(termTypeInferrer: => TermTypeInferrer,
                                                         tupleTypeInferrer: => TupleTypeInferrer,
                                                         collectiveTypeInferrer: CollectiveTypeInferrer)
  extends TermArgsToTypeArgsInferrer {

  override def infer(termArgs: List[Term]): List[Type] = {
    termArgs match {
      case tupleArgs if tupleArgs.forall(_.isInstanceOf[Term.Tuple]) =>
        inferCollectiveTupleType(tupleArgs.map(_.asInstanceOf[Term.Tuple]))
      case scalarArgs => List(inferCollectiveScalarType(scalarArgs))
    }
  }

  private def inferCollectiveTupleType(tupleArgs: List[Term.Tuple]): List[Type] = {
    collectiveTypeInferrer.inferTuple(tupleArgs.map(tupleTypeInferrer.infer)).args
  }

  private def inferCollectiveScalarType(args: List[Term]): Type = {
    collectiveTypeInferrer.inferScalar(args.map(termTypeInferrer.infer)).getOrElse(Type.Name("Any"))
  }
}