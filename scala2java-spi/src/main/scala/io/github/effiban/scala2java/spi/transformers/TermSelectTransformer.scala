package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermSelectTransformer {
  def transform(termSelect: Term.Select): Term.Select
}


object TermSelectTransformer {
  val Identity: TermSelectTransformer = identity[Term.Select]
}

