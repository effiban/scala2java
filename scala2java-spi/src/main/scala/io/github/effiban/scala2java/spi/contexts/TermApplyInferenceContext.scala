package io.github.effiban.scala2java.spi.contexts

import scala.meta.Type

/** Holds a context with types needed for inferring the method signature corresponding to a [[scala.meta.Term.Apply]] (method invocation)
 *
 * @param maybeParentType the inferred type of the parent of the [[scala.meta.Term.Apply]], if available
 * @param maybeArgTypes the inferred type of the arguments of the [[scala.meta.Term.Apply]], if available
 */
case class TermApplyInferenceContext(maybeParentType: Option[Type] = None, maybeArgTypes: List[Option[Type]] = Nil)
