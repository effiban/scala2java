package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermApplyTypeToTermApplyTransformer {

  def transform(applyType: Term.ApplyType): Option[Term.Apply]
}

object TermApplyTypeToTermApplyTransformer {
  def Empty: TermApplyTypeToTermApplyTransformer = _ => None
}
