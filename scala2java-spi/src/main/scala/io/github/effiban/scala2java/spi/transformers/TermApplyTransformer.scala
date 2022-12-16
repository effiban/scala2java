package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

trait TermApplyTransformer extends SameTypeTransformer[Term.Apply]

object TermApplyTransformer {
  val Identity: TermApplyTransformer = identity[Term.Apply]
}
