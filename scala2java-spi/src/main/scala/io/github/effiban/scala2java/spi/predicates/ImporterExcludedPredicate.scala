package io.github.effiban.scala2java.spi.predicates

import scala.meta.Importer

trait ImporterExcludedPredicate extends (Importer => Boolean)

object ImporterExcludedPredicate {
  val None: ImporterExcludedPredicate = _ => false
}
