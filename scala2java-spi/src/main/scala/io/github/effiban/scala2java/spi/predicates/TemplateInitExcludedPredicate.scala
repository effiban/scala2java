package io.github.effiban.scala2java.spi.predicates

import scala.meta.Init

/** A predicate which determines whether a given [[Init]] (parent type of a `class` or `trait`) appearing in the Scala source file,
 * should be excluded from any class or interface in the generated Java file.
 */
@deprecated
trait TemplateInitExcludedPredicate extends (Init => Boolean)

@deprecated
object TemplateInitExcludedPredicate {
  /** The default predicate which does not exclude any [[Init]]-s */
  val None: TemplateInitExcludedPredicate = _ => false
}