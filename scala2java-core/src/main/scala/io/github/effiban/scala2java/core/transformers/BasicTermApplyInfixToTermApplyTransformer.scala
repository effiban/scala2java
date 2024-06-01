package io.github.effiban.scala2java.core.transformers
import io.github.effiban.scala2java.spi.transformers.TermApplyInfixToTermApplyTransformer

import scala.meta.Term

object BasicTermApplyInfixToTermApplyTransformer extends TermApplyInfixToTermApplyTransformer {

  override def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply] = {
    import termApplyInfix._
    //TODO handle type args
    Some(Term.Apply(fun = Term.Select(lhs, op), args = args))
  }
}
