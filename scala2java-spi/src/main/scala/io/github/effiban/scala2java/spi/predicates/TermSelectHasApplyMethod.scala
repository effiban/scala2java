package io.github.effiban.scala2java.spi.predicates

import scala.meta.Term

/** A predicate which determines whether a given [[Term.Select]] appearing in the Scala source file,
 * has an `apply()` method (whether defined implicitly or explicitly).<br>
 * This is true of all case classes and functions, and also some companion objects.<br>
 * The framework will use this in order to properly identify invocations where the `apply()`
 * does not appear explicitly.
 */
// TODO - once file-scope or external type inference is added, add ability to check by a given type as well
@deprecated
trait TermSelectHasApplyMethod extends (Term.Select => Boolean)

@deprecated
object TermSelectHasApplyMethod {
  /** The default predicate which returns false, meaning none have an `apply()` method */
  val None: TermSelectHasApplyMethod = _ => false
}