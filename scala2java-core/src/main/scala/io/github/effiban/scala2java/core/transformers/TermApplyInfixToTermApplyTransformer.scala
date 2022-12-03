package io.github.effiban.scala2java.core.transformers

import scala.meta.Term

trait TermApplyInfixToTermApplyTransformer {
  def transform(termApplyInfix: Term.ApplyInfix): Option[Term.Apply]
}

object TermApplyInfixToTermApplyTransformer {
  val Empty: TermApplyInfixToTermApplyTransformer = _ => None
}
