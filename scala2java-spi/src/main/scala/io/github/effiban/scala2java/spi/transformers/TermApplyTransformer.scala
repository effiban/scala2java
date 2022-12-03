package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermApplyTransformer {
  def transform(termApply: Term.Apply): Term.Apply
}

object TermApplyTransformer {
  val Identity: TermApplyTransformer = identity[Term.Apply]
}
