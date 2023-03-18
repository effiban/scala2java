package io.github.effiban.scala2java.spi.contexts

import scala.meta.Type

/** Holds a context with extra information needed for inferring the type of a [[scala.meta.Term.Select]]
 *
 * @param maybeQualType the inferred type of the qualifier of the [[scala.meta.Term.Select]], if available
 */
case class TermSelectInferenceContext(maybeQualType: Option[Type] = None)
