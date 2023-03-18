package io.github.effiban.scala2java.spi.contexts

import scala.meta.Type

/** Holds a context with extra information needed for transforming a [[scala.meta.Term.Select]] to a Java-style term
 *
 * @param maybeQualType the inferred type of the qualifier of the [[scala.meta.Term.Select]], if available
 */
case class TermSelectTransformationContext(maybeQualType: Option[Type] = None)
