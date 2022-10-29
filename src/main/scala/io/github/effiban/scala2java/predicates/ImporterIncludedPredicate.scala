package io.github.effiban.scala2java.predicates

import io.github.effiban.scala2java.classifiers.ImporterClassifier

import scala.meta.Importer

trait ImporterIncludedPredicate extends (Importer => Boolean)

class ImporterIncludedPredicateImpl(importerClassifier: ImporterClassifier) extends ImporterIncludedPredicate {

  // TODO instead of filtering out all scala imports, some can be converted to Java equivalents
  override def apply(importer: Importer): Boolean = !importerClassifier.isScala(importer)
}

object ImporterIncludedPredicate extends ImporterIncludedPredicateImpl(ImporterClassifier)
