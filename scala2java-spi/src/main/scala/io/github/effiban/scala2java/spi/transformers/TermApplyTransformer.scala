package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

/** A transformer which can modify a given Scala [[Term.Apply]] (method invocation) */
trait TermApplyTransformer extends SameTypeTransformer[Term.Apply]

object TermApplyTransformer {
  /** The default transformer which returns the [[Term.Apply]] unchanged, indicating that no transformation is needed. */
  val Identity: TermApplyTransformer = identity[Term.Apply]
}
