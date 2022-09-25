package effiban.scala2java.typeinference

import scala.meta.{Term, Type}

trait TupleTypeInferrer {
  def infer(termTuple: Term.Tuple): Type.Tuple
}

private[typeinference] class TupleTypeInferrerImpl(termTypeInferrer: => TermTypeInferrer) extends TupleTypeInferrer {

  override def infer(termTuple: Term.Tuple): Type.Tuple = {
    val types = termTuple.args.map(term => termTypeInferrer.infer(term).getOrElse(Type.Name("Any")))
    Type.Tuple(types)
  }
}
