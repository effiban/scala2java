package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectContext

import scala.meta.Term

/** A transformer which can modify a given Scala [[Term.Select]] (qualified name).<br>
 * The transformer also receives a context object with more information about the qualified name,
 * such as the inferred type of the qualifier.<br>
 * This is useful for cases where the Java equivalent has a different qualifier+name combination.
 */
trait TermSelectTransformer extends SameTypeTransformer1[Term.Select, TermSelectContext]


object TermSelectTransformer {
  /** The default transformer which returns the input [[Term.Select]] unchanged, indicating that no transformation is needed. */
  val Identity: TermSelectTransformer = (select, _) => select
}

