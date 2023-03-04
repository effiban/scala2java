package io.github.effiban.scala2java.spi.contexts

import scala.meta.Type

/** Holds a context with extra information related to a [[scala.meta.Term.Select]]
 *
 * @param appliedTypeArgs type arguments applied to an enclosing [[scala.meta.Term.ApplyType]],
 *                        which in Java must be applied to the inner [[scala.meta.Term.Select]]
 * @param maybeQualType the inferred type of the qualifier of the [[scala.meta.Term.Select]], if available
 */
case class TermSelectContext(appliedTypeArgs: List[Type] = Nil, maybeQualType: Option[Type] = None)
