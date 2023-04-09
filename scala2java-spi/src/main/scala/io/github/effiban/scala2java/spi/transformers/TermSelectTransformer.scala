package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.TermSelectTransformationContext

import scala.meta.Term

/** A transformer which can convert a given Scala [[Term.Select]] (qualified name) into any [[Term]].<br>
 * Usually the output will also be a [[Term.Select]], but there are cases where the Java equivalent is a different tree type.<br>
 * The transformer also receives a context object with extra information needed for a precise transformation,
 * such as the inferred type of the qualifier.<br>
 */
trait TermSelectTransformer extends DifferentTypeTransformer1[Term.Select, TermSelectTransformationContext, Term]


object TermSelectTransformer {
  /** The default transformer which returns the None, indicating that no transformation is needed. */
  val Empty: TermSelectTransformer = (_, _) => None
}

