package io.github.effiban.scala2java.spi.predicates

import scala.meta.Importer

trait ImporterIncludedPredicate extends (Importer => Boolean)

object ImporterIncludedPredicate {
  val All: ImporterIncludedPredicate = _ => true
}
