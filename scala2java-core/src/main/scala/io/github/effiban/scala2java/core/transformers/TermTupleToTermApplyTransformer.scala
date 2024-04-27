package io.github.effiban.scala2java.core.transformers

import scala.meta.{Term, XtensionQuasiquoteTerm}

trait TermTupleToTermApplyTransformer {

  def transform(termTuple: Term.Tuple): Term.Apply
}

object TermTupleToTermApplyTransformer extends TermTupleToTermApplyTransformer {

  override def transform(termTuple: Term.Tuple): Term.Apply = {
    termTuple.args match {
      // 0 or 1 arg are both impossible - would fail parsing of the code before we get here
      // For a tuple of 2, using Java's Map.entry()
      case arg1 :: arg2 :: Nil => Term.Apply(fun = q"java.util.Map.entry", args = List(arg1, arg2))
      // Java has no Tuple term (or generating method) for 3+ params, so we will use JOOL's Tuple.tuple()
      case args => Term.Apply(fun = q"org.jooq.lambda.tuple.Tuple.tuple", args = args)
    }
  }
}
