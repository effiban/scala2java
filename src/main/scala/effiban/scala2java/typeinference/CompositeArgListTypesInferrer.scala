package effiban.scala2java.typeinference

import effiban.scala2java.classifiers.TermTypeClassifier
import effiban.scala2java.transformers.TermToTupleCaster

import scala.meta.{Term, Type}

trait CompositeArgListTypesInferrer {
  def infer(args: List[Term]): List[Type]
}

private[typeinference] class CompositeArgListTypesInferrerImpl(scalarArgListTypeInferrer: => ScalarArgListTypeInferrer,
                                                               tupleArgListTypesInferrer: => TupleArgListTypesInferrer,
                                                               termTypeClassifier: => TermTypeClassifier,
                                                               termToTupleCaster: TermToTupleCaster) extends CompositeArgListTypesInferrer {

  override def infer(args: List[Term]): List[Type] = {
    if (allAreTuples(args)) {
      tupleArgListTypesInferrer.infer(args.map(termToTupleCaster.cast))
    } else {
      List(scalarArgListTypeInferrer.infer(args))
    }
  }

  private def allAreTuples(args: List[Term]): Boolean = args.forall(termTypeClassifier.isTupleLike)
}