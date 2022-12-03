package io.github.effiban.scala2java.core.transformers
import scala.meta.Term

object BasicTermApplyInfixToTermApplyTransformer extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    import termApplyInfix._
    //TODO handle type args
    Some(Term.Apply(fun = op, args = lhs +: args))
  }
}
