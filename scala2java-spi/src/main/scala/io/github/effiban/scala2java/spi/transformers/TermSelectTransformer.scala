package io.github.effiban.scala2java.spi.transformers

import scala.meta.Term

/** A transformer which can modify a given Scala [[Term.Select]] (qualified name) */
trait TermSelectTransformer extends SameTypeTransformer0[Term.Select]


object TermSelectTransformer {
  /** The default transformer which returns the [[Term.Select]] unchanged, indicating that no transformation is needed. */
  val Identity: TermSelectTransformer = identity[Term.Select]
}

