package io.github.effiban.scala2java.core.predicates

import io.github.effiban.scala2java.core.classifiers.ImporterClassifier
import io.github.effiban.scala2java.spi.predicates.ImporterIncludedPredicate

import scala.meta.Importer

class CoreImporterIncludedPredicate(importerClassifier: ImporterClassifier) extends ImporterIncludedPredicate {

  // TODO instead of filtering out all scala imports, some can be converted to Java equivalents
  override def apply(importer: Importer): Boolean = !importerClassifier.isScala(importer)
}

object CoreImporterIncludedPredicate extends CoreImporterIncludedPredicate(ImporterClassifier)
