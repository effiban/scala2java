package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermApplyTypeToTermApplyTransformer extends DifferentTypeTransformer[Term.ApplyType, Term.Apply]

object TermApplyTypeToTermApplyTransformer {
  def Empty: TermApplyTypeToTermApplyTransformer = _ => None
}
