package effiban.scala2java.transformers

import scala.meta.Term

trait TermTupleToTermApplyTransformer {

  def transform(termTuple: Term.Tuple): Term.Apply
}

object TermTupleToTermApplyTransformer extends TermTupleToTermApplyTransformer {

  override def transform(termTuple: Term.Tuple): Term.Apply = {
    termTuple.args match {
      // 0 or 1 arg are both impossible - would fail parsing of the code before we get here
      case arg1 :: arg2 :: Nil => Term.Apply(fun = Term.Select(Term.Name("Map"), Term.Name("entry")), args = List(arg1, arg2))
      // Java has no general Tuple type (or generating method), so for 3+ params we will use JOOL's Tuple
      case args => Term.Apply(fun = Term.Select(Term.Name("Tuple"), Term.Name("tuple")), args = args)
    }
  }
}
