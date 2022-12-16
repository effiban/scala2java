package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermSelectTransformer extends SameTypeTransformer[Term.Select]


object TermSelectTransformer {
  val Identity: TermSelectTransformer = identity[Term.Select]
}

