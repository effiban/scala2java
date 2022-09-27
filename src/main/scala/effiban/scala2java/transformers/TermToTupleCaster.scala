package effiban.scala2java.transformers

import scala.meta.Term

trait TermToTupleCaster {

  def cast(term: Term): Term.Tuple
}

object TermToTupleCaster extends TermToTupleCaster {

  override def cast(term: Term): Term.Tuple = term match {
    case tuple: Term.Tuple => tuple
    // Here we can assume that the infix was examined previously in the flow using TermTypeClassifier (not failing unless we have to)
    case applyInfix: Term.ApplyInfix => Term.Tuple(List(applyInfix.lhs) ++ applyInfix.args)
    case nonTuple => throw new IllegalStateException(s"Got an unexpected term $nonTuple which cannot be casted to a tuple")
  }
}
