package io.github.effiban.scala2java.spi.predicates

import scala.meta.Importer

/** A predicate which determines whether a given [[Importer]]s (individual `import` statement) from the Scala source file,
 * should be excluded from the generated Java source file.
 */
trait ImporterExcludedPredicate extends (Importer => Boolean)

object ImporterExcludedPredicate {
  /** The default predicate which does not exclude any [[Importer]] */
  val None: ImporterExcludedPredicate = _ => false
}
