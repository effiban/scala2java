package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.TermApplyTransformationContext

import scala.meta.Term

/** A transformer which can modify a given Scala [[Term.Apply]] (method invocation).<br>
 * The transformer also receives a context object with more information about the method invocation,
 * such as the inferred types of the method name and its arguments.<br>
 * This is useful for example in cases where the Java equivalent has a different method signature.
 * */
trait TermApplyTransformer extends SameTypeTransformer1[Term.Apply, TermApplyTransformationContext]

object TermApplyTransformer {
  /** The default transformer which returns the [[Term.Apply]] unchanged, indicating that no transformation is needed. */
  val Identity: TermApplyTransformer = (termApply, _) => termApply
}
