package io.github.effiban.scala2java.spi.transformers

import io.github.effiban.scala2java.spi.contexts.UnqualifiedTermApplyTransformationContext
import io.github.effiban.scala2java.spi.entities.UnqualifiedTermApply

import scala.meta.Term

/** A transformer which can transform an [[UnqualifiedTermApply]] into a Java equivalent.<br>
 * An [[UnqualifiedTermApply]] represents a method invocation that has a qualified name, but without the qualifier part.<br>
 * It should be overriden whenever a method invocation depends separately on the '''type'''
 * of qualifier and the name, so that each can be transformed separately.<br>
 * It receives a context object with additional information about the method invocation which is required to properly identify it.<br>
 * '''NOTE''' that in order to handle the recursive transformation properly, this transformer will be called only after
 * all of the members in the input [[UnqualifiedTermApply]] have been transformed to Java, except for the method name itself.
 */
trait UnqualifiedTermApplyTransformer extends DifferentTypeTransformer1[
  UnqualifiedTermApply, UnqualifiedTermApplyTransformationContext, UnqualifiedTermApply
]

object UnqualifiedTermApplyTransformer {

  /** The default transformer which returns empty, indicating that no transformation is needed. */
  val Empty: UnqualifiedTermApplyTransformer = (_, _) => None
}
