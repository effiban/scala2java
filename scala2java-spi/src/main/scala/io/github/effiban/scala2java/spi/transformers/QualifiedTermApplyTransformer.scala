package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.{QualifiedTermApplyTransformationContext, UnqualifiedTermApplyTransformationContext}
import io.github.effiban.scala2java.spi.entities.QualifiedTermApply

import scala.meta.Term

/** A transformer which can modify a qualified [[Term.Apply]] (method invocation) to a Java equivalent. */
trait QualifiedTermApplyTransformer extends DifferentTypeTransformer1[QualifiedTermApply, QualifiedTermApplyTransformationContext, QualifiedTermApply]

object QualifiedTermApplyTransformer {
  /** The default transformer which returns empty, indicating that no transformation is needed. */
  val Empty: QualifiedTermApplyTransformer = (_, _) => None
}
