package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext

import scala.meta.Term

/** A transformer which can convert a given Scala [[Term.Select]] (qualified name) into any [[Term]].<br>
 * Usually the output will also be a [[Term.Select]], but there are cases where the Java equivalent is a different tree type.<br>
 */
trait TermSelectTransformer extends DifferentTypeTransformer0[Term.Select, Term]

object TermSelectTransformer {
  /** The default transformer which returns None, indicating that no transformation is needed. */
  val Empty: TermSelectTransformer = _ => None
}

