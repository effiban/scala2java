package io.github.effiban.scala2java.spi.predicates

import io.github.effiban.scala2java.spi.entities.InvocationArgCoordinates

/** A predicate which determines whether an invocation argument identified ("located") by the given [[InvocationArgCoordinates]],
 * is expected to be passed by-name.<br>
 * This is required because Java does not support passing arguments by-name, and they must be wrapped in a Java Supplier
 * in order to achieve the same "lazy" logic.
 *
 * @see [[InvocationArgCoordinates]]
 */
trait InvocationArgByNamePredicate extends (InvocationArgCoordinates => Boolean)

object InvocationArgByNamePredicate {
  /** The default predicate which always returns `false`, i.e. "by-value" */
  val Default: InvocationArgByNamePredicate = _ => false
}